package com.gaurav.astrokit.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize
import com.gaurav.astrokit.AstroKitEngine
import com.gaurav.astrokit.AstroProfile
import com.gaurav.astrokit.astro.BestMatches
import com.gaurav.astrokit.astro.CompatibilityEngine
import com.gaurav.astrokit.astro.CompatibilityResult
import com.gaurav.astrokit.astro.ZodiacSign
import com.gaurav.astrokit.catalog.DailyInsight
import com.gaurav.astrokit.catalog.DailyInsightGenerator
import com.gaurav.astrokit.catalog.InsightsCatalog
import com.gaurav.astrokit.platform.copyToClipboard
import com.gaurav.astrokit.platform.todayKey
import com.gaurav.astrokit.storage.InsightStore
import com.gaurav.astrokit.storage.ProfilesStore
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AstroHomeScreen(contentPadding: PaddingValues) {

    // ---------- Multi-profile state ----------
    val profiles = remember { mutableStateListOf<UserProfile>() }
    var selectedProfile by remember { mutableStateOf<UserProfile?>(null) }

    fun reloadProfiles(keepSelectedId: String? = ProfilesStore.getSelectedId()) {
        val loaded = ProfilesStore.loadProfiles()
        profiles.clear()
        profiles.addAll(loaded)

        val pick = loaded.firstOrNull { it.id == keepSelectedId } ?: loaded.firstOrNull()
        selectedProfile = pick
        pick?.let { ProfilesStore.setSelectedId(it.id) }
    }

    LaunchedEffect(Unit) {
        ProfilesStore.ensureSeeded()
        reloadProfiles()
    }

    // ---------- Core screen state ----------
    var profile by remember { mutableStateOf<AstroProfile?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var partner by remember { mutableStateOf<ZodiacSign?>(null) }
    var compatibility by remember { mutableStateOf<CompatibilityResult?>(null) }

    // ---------- Add profile dialog state ----------
    var showAddProfileDialog by remember { mutableStateOf(false) }
    var addName by remember { mutableStateOf("") }
    var addDob by remember { mutableStateOf<Dob?>(null) }
    var showAddDobPicker by remember { mutableStateOf(false) }
    val addDobPickerState = rememberDatePickerState()

    // ---------- Delete profile dialog state ----------
    var showDeleteProfileDialog by remember { mutableStateOf(false) }
    var profileToDelete by remember { mutableStateOf<UserProfile?>(null) }

    // ---------- Staggered reveal flags ----------
    var showStats by remember { mutableStateOf(false) }
    var showZodiacDetails by remember { mutableStateOf(false) }
    var showDaily by remember { mutableStateOf(false) }
    var showInsights by remember { mutableStateOf(false) }
    var showCompatibility by remember { mutableStateOf(false) }
    var showBestMatches by remember { mutableStateOf(false) }

    fun resetReveals() {
        showStats = false
        showZodiacDetails = false
        showDaily = false
        showInsights = false
        showCompatibility = false
        showBestMatches = false
    }

    suspend fun playRevealSequence() {
        resetReveals()
        delay(120); showStats = true
        delay(120); showZodiacDetails = true
        delay(120); showDaily = true
        delay(120); showInsights = true
        delay(120); showCompatibility = true
        delay(120); showBestMatches = true
    }

    fun resetComputedState() {
        profile = null
        partner = null
        compatibility = null
        error = null
        resetReveals()
    }

    fun compute() {
        error = null
        compatibility = null
        resetReveals()

        val d = selectedProfile?.dob
        if (d == null) {
            error = "Please select a profile and date of birth."
            return
        }

        profile = AstroKitEngine.computeProfile(d.year, d.month, d.day)
    }

    fun computeCompatibility() {
        val p = profile ?: return
        val other = partner ?: run {
            error = "Pick a partner sign to calculate compatibility."
            return
        }
        error = null
        compatibility = CompatibilityEngine.between(p.zodiac, other)
    }

    LaunchedEffect(profile) {
        if (profile != null) playRevealSequence()
    }

    // ---------- Main DOB picker (for selected profile) ----------
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    val sp = selectedProfile
                    if (millis != null && sp != null) {
                        val newDob = epochMillisToDob(millis)
                        ProfilesStore.updateDob(sp.id, newDob)
                        reloadProfiles(sp.id)
                        resetComputedState()
                    }
                    showDatePicker = false
                }) { Text("Done") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    // ---------- Add profile DOB picker ----------
    if (showAddDobPicker) {
        DatePickerDialog(
            onDismissRequest = { showAddDobPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = addDobPickerState.selectedDateMillis
                    if (millis != null) {
                        addDob = epochMillisToDob(millis)
                    }
                    showAddDobPicker = false
                }) { Text("Done") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDobPicker = false }) { Text("Cancel") }
            }
        ) { DatePicker(state = addDobPickerState) }
    }

    // ---------- Add profile dialog ----------
    if (showAddProfileDialog) {
        AlertDialog(
            onDismissRequest = { showAddProfileDialog = false },
            title = { Text("Add profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = addName,
                        onValueChange = { addName = it },
                        label = { Text("Profile name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssistChip(
                            onClick = { showAddDobPicker = true },
                            label = {
                                Text(
                                    text = addDob?.format()?.let { "DOB: $it" } ?: "Select DOB",
                                    maxLines = 1
                                )
                            }
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = addDob?.format() ?: "",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Text(
                        "Tip: Add Partner/Child to personalize compatibility and daily insights.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val dob = addDob
                    if (dob == null) {
                        // keep dialog open, show error via snackbar-ish chip at top level
                        error = "Select DOB to add profile."
                        return@TextButton
                    }
                    val created = ProfilesStore.addProfile(addName, dob)
                    reloadProfiles(created.id)

                    // reset dialog state
                    addName = ""
                    addDob = null
                    showAddProfileDialog = false

                    resetComputedState()
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddProfileDialog = false
                    addName = ""
                    addDob = null
                }) { Text("Cancel") }
            }
        )
    }

    // ---------- Delete profile dialog ----------
    if (showDeleteProfileDialog) {
        val p = profileToDelete
        AlertDialog(
            onDismissRequest = {
                showDeleteProfileDialog = false
                profileToDelete = null
            },
            title = { Text("Delete profile?") },
            text = {
                Text(
                    if (p == null) "This action cannot be undone."
                    else "Delete “${p.name}” (${p.dob.format()})? This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val del = profileToDelete
                    if (del != null) {
                        ProfilesStore.deleteProfile(del.id)
                        reloadProfiles()
                        resetComputedState()
                    }
                    showDeleteProfileDialog = false
                    profileToDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteProfileDialog = false
                    profileToDelete = null
                }) { Text("Cancel") }
            }
        )
    }

    // ---------- UI ----------
    Scaffold() { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                HeroCardPremium(
                    dobText = selectedProfile?.dob?.format(),
                    profile = profile,
                    onChangeDob = { showDatePicker = true },
                    onCompute = { compute() }
                )
            }

            item {
                ProfileChipsRow(
                    profiles = profiles,
                    selectedId = selectedProfile?.id,
                    onSelect = { p ->
                        ProfilesStore.setSelectedId(p.id)
                        selectedProfile = p
                        resetComputedState()
                    },
                    onDelete = { p ->
                        profileToDelete = p
                        showDeleteProfileDialog = true
                    }
                )
            }

            item {
                val sp = selectedProfile
                if (sp != null) {
                    SavedProfileRow(
                        label = sp.name,
                        dobText = sp.dob.format(),
                        onChangeDob = { showDatePicker = true },
                        onDelete = {
                            profileToDelete = sp
                            showDeleteProfileDialog = true
                        }
                    )
                }
            }

            error?.let { err ->
                item { AssistChip(onClick = {}, label = { Text(err) }) }
            }

            item {
                val p = profile
                if (p != null) {
                    ResultSectionPremium(
                        profile = p,
                        partner = partner,
                        onPartnerChange = { partner = it; compatibility = null },
                        compatibility = compatibility,
                        onComputeCompatibility = { computeCompatibility() },
                        showStats = showStats,
                        showZodiacDetails = showZodiacDetails,
                        showDaily = showDaily,
                        showInsights = showInsights,
                        showCompatibility = showCompatibility,
                        showBestMatches = showBestMatches
                    )
                }
            }
        }
    }
}

/* ---------------------------- HERO CARD ---------------------------- */

@Composable
private fun ZodiacCalcExplainerCard() {
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "How we calculated your zodiac",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "DOB-based Sun sign (offline)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(if (expanded) "▴" else "▾")
            }

            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "We calculate your zodiac using your date of birth only. This is the standard “Sun sign” method used in Western (tropical) astrology — it maps your birth date to the Sun’s sign date ranges.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Not included (yet): birth time and location. Those are required for rising sign (Ascendant), moon sign, houses, and a full Vedic birth chart (Kundli).",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun WhyThisMatchCard(
    self: ZodiacSign,
    partner: ZodiacSign,
    accent: androidx.compose.ui.graphics.Color
) {
    val why = remember(self, partner) {
        com.gaurav.astrokit.astro.CompatibilityWhyEngine.explain(self, partner)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Why this match?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            ScoreLine(
                label = "Element harmony",
                value = why.elementScore,
                accent = accent
            )
            ScoreLine(
                label = "Modality dynamic",
                value = why.modalityScore,
                accent = accent
            )

            Divider()

            Text(why.overallHint, style = MaterialTheme.typography.bodyMedium)

            why.bullets.forEach { b ->
                Text("• $b", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun ScoreLine(
    label: String,
    value: Int,
    accent: androidx.compose.ui.graphics.Color
) {
    val p = (value.coerceIn(0, 100)) / 100f
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            LinearProgressIndicator(
                progress = { p },
                color = accent,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Text("$value%", fontWeight = FontWeight.SemiBold, color = accent)
    }
}


@Composable
private fun HeroCardPremium(
    dobText: String?,
    profile: AstroProfile?,
    onChangeDob: () -> Unit,
    onCompute: () -> Unit
) {
    val cs = MaterialTheme.colorScheme

    val defaultGradient = remember(cs) {
        androidx.compose.ui.graphics.Brush.linearGradient(
            listOf(
                cs.primary.copy(alpha = 0.85f),
                cs.secondary.copy(alpha = 0.65f),
                cs.tertiary.copy(alpha = 0.55f)
            )
        )
    }

    val gradient = remember(profile, defaultGradient) {
        if (profile == null) defaultGradient else PremiumStyles.elementGradient(profile.zodiac.element)
    }

    val headerTitle =
        if (profile == null) "Cosmic + Life Math"
        else "${profile.zodiac.displayName} • ${profile.zodiac.element}"

    val headerSubtitle =
        if (profile == null) "Zodiac • Life Path • Personal Year • Compatibility"
        else "Life Path ${profile.lifePath} • Personal Year ${profile.personalYear}"

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("✦ ✧ ✦", color = cs.onPrimary.copy(alpha = 0.85f))
                Text("✧ ✦ ✧", color = cs.onPrimary.copy(alpha = 0.75f))
            }

            Text(
                headerTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = cs.onPrimary
            )
            Text(
                headerSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = cs.onPrimary.copy(alpha = 0.9f)
            )

            SuggestionChip(
                onClick = onChangeDob,
                label = { Text(if (dobText != null) "DOB: $dobText" else "Select date of birth") }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onChangeDob, modifier = Modifier.weight(1f)) { Text("Change DOB") }
                FilledTonalButton(onClick = onCompute, modifier = Modifier.weight(1f)) { Text("Compute") }
            }
        }
    }
}

/* ---------------------------- PROFILE ROWS ---------------------------- */

@Composable
private fun ProfileChipsRow(
    profiles: List<UserProfile>,
    selectedId: String?,
    onSelect: (UserProfile) -> Unit,
    onDelete: (UserProfile) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        profiles.forEach { p ->
            val selected = p.id == selectedId
            val label = if (selected) "✓ ${p.name}" else p.name

            AssistChip(
                onClick = { onSelect(p) },
                label = { Text(label, maxLines = 1) },
                trailingIcon = {
                    // Prevent deleting the last remaining profile
                    if (profiles.size > 1) {
                        Text(
                            "✕",
                            modifier = Modifier
                                .clickable { onDelete(p) }
                                .padding(start = 8.dp)
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun SavedProfileRow(
    label: String,
    dobText: String,
    onChangeDob: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssistChip(onClick = {}, label = { Text("$label: $dobText", maxLines = 1) })
        AssistChip(onClick = onChangeDob, label = { Text("Change DOB", maxLines = 1) })
        FilledTonalIconButton(onClick = onDelete) {
            Text("✕", style = MaterialTheme.typography.titleMedium)
        }
    }
}

/* ---------------------------- RESULTS + ANIMATIONS ---------------------------- */

@Composable
private fun ResultSectionPremium(
    profile: AstroProfile,
    partner: ZodiacSign?,
    onPartnerChange: (ZodiacSign?) -> Unit,
    compatibility: CompatibilityResult?,
    onComputeCompatibility: () -> Unit,
    showStats: Boolean,
    showZodiacDetails: Boolean,
    showDaily: Boolean,
    showInsights: Boolean,
    showCompatibility: Boolean,
    showBestMatches: Boolean
) {
    val accent = PremiumStyles.elementAccent(profile.zodiac.element)

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

        AnimatedBlock(visible = showStats) {
            StatRow(
                leftTitle = "Zodiac",
                leftValue = profile.zodiac.displayName,
                rightTitle = "Life Path",
                rightValue = profile.lifePath.toString()
            )
        }

        AnimatedBlock(visible = showZodiacDetails) {
            DetailCard(
                title = "Zodiac details",
                lines = listOf(
                    "Element: ${profile.zodiac.element}",
                    "Modality: ${profile.zodiac.modality}",
                    "Ruling planet: ${profile.zodiac.rulingPlanet}"
                )
            )
        }

        AnimatedBlock(visible = showZodiacDetails) {
            ZodiacCalcExplainerCard()
        }


        AnimatedBlock(visible = showDaily) {
            val daily = remember(profile) { DailyInsightGenerator.generate(profile) }
            DailyInsightCard(profile = profile, insight = daily)
        }

        AnimatedBlock(visible = showInsights) {
            val z = InsightsCatalog.zodiacTraits(profile.zodiac)
            val lp = InsightsCatalog.lifePathInsight(profile.lifePath)
            val py = InsightsCatalog.personalYearInsight(profile.personalYear)

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InsightCard(z.title, z.bullets)
                InsightCard(lp.title, lp.bullets)
                InsightCard(py.title, py.bullets)
            }
        }

        AnimatedBlock(visible = showCompatibility) {
            CompatibilityCard(
                self = profile.zodiac,
                partner = partner,
                onPartnerChange = onPartnerChange,
                onCompute = onComputeCompatibility,
                compatibility = compatibility,
                accent = accent
            )
        }

        AnimatedBlock(visible = showBestMatches) {
            BestMatchesCard(self = profile.zodiac)
        }
    }
}

@Composable
private fun AnimatedBlock(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(280)) + slideInVertically(tween(280)) { it / 10 },
        exit = fadeOut(tween(180)) + slideOutVertically(tween(180)) { it / 14 }
    ) { content() }
}

@Composable
private fun StatRow(leftTitle: String, leftValue: String, rightTitle: String, rightValue: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        StatCard(title = leftTitle, value = leftValue, modifier = Modifier.weight(1f))
        StatCard(title = rightTitle, value = rightValue, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun DetailCard(title: String, lines: List<String>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            lines.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

@Composable
private fun InsightCard(title: String, bullets: List<String>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            bullets.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

/* ---------------------------- DAILY INSIGHT (SAVE + SHARE) ---------------------------- */

@Composable
private fun DailyInsightCard(profile: AstroProfile, insight: DailyInsight) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(insight.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(insight.message, style = MaterialTheme.typography.bodyMedium)
            Divider()
            Text("Focus: ${insight.focus}", style = MaterialTheme.typography.bodyMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                FilledTonalButton(
                    onClick = { InsightStore.save(todayKey(), insight) },
                    modifier = Modifier.weight(1f)
                ) { Text("Save") }

                Button(
                    onClick = {
                        val share = DailyInsightGenerator.shareText(profile, insight)
                        copyToClipboard("AstroKit Insight", share)
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Share") }
            }
        }
    }
}

/* ---------------------------- COMPATIBILITY ---------------------------- */

@Composable
private fun CompatibilityCard(
    self: ZodiacSign,
    partner: ZodiacSign?,
    onPartnerChange: (ZodiacSign?) -> Unit,
    onCompute: () -> Unit,
    compatibility: CompatibilityResult?,
    accent: androidx.compose.ui.graphics.Color
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Compatibility", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("You are ${self.displayName}. Pick a partner sign:", style = MaterialTheme.typography.bodyMedium)

            ZodiacDropdownStable(selected = partner, onSelected = onPartnerChange)

            FilledTonalButton(onClick = onCompute) { Text("Calculate") }

            compatibility?.let { res ->
                Divider()
                CompatibilityVisual(result = res, accent = accent)
                Spacer(Modifier.height(8.dp))
                Text(res.summary, style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(12.dp))

                // ✅ Why this match section (only when partner chosen)
                WhyThisMatchCard(
                    self = self,
                    partner = partner ?: return@let,
                    accent = accent
                )
            }

        }
    }
}

@Composable
private fun CompatibilityVisual(result: CompatibilityResult, accent: androidx.compose.ui.graphics.Color) {
    val target = (result.score.coerceIn(0, 100)) / 100f
    val animatedProgress by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = 700),
        label = "compat-progress"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { animatedProgress },
                strokeWidth = 8.dp,
                strokeCap = StrokeCap.Round,
                color = accent
            )
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("${result.score}", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
            Text(result.label, style = MaterialTheme.typography.titleMedium)
            LinearProgressIndicator(
                progress = { animatedProgress },
                color = accent,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Stable dropdown for KMP.
 */
@Composable
private fun ZodiacDropdownStable(
    selected: ZodiacSign?,
    onSelected: (ZodiacSign?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val signs = remember { ZodiacSign.values().toList() }

    var textFieldSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }
    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxWidth()) {

        OutlinedTextField(
            value = selected?.displayName ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Partner sign") },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) { Text("▾") }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                }
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(with(density) { textFieldSize.width.toDp() })
        ) {
            signs.forEach { sign ->
                DropdownMenuItem(
                    text = { Text(sign.displayName) },
                    onClick = {
                        onSelected(sign)
                        expanded = false
                    }
                )
            }
        }
    }
}

/* ---------------------------- BEST MATCHES ---------------------------- */

@Composable
private fun BestMatchesCard(self: ZodiacSign) {
    val matches = remember(self) { BestMatches.top3(self) }
    val accent = PremiumStyles.elementAccent(self.element)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Top matches for ${self.displayName}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            matches.forEachIndexed { idx, m ->
                MatchRow(rank = idx + 1, sign = m.sign.displayName, score = m.result.score, accent = accent)
                if (idx != matches.lastIndex) Divider()
            }
        }
    }
}

@Composable
private fun MatchRow(rank: Int, sign: String, score: Int, accent: androidx.compose.ui.graphics.Color) {
    val target = (score.coerceIn(0, 100)) / 100f
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("$rank. $sign", style = MaterialTheme.typography.titleMedium)
            LinearProgressIndicator(
                progress = { target },
                color = accent,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Text("$score/100", color = accent, fontWeight = FontWeight.SemiBold)
    }
}
