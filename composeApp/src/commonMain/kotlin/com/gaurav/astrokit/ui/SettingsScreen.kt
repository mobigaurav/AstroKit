package com.gaurav.astrokit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gaurav.astrokit.platform.copyToClipboard
import com.gaurav.astrokit.platform.openUrl
import com.gaurav.astrokit.platform.rateApp
import com.gaurav.astrokit.platform.shareText
import com.gaurav.astrokit.storage.InsightStore
import com.gaurav.astrokit.storage.ProfilesStore
import com.gaurav.astrokit.platform.getAppInfo
import com.gaurav.astrokit.storage.BirthDetailsStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    contentPadding: PaddingValues,
    onOpenBirthDetails: () -> Unit,
    onOpenKundli: () -> Unit
) {

    val profiles = remember { mutableStateListOf<UserProfile>() }
    var selectedId by remember { mutableStateOf<String?>(null) }

    fun reload() {
        ProfilesStore.ensureSeeded()
        val loaded = ProfilesStore.loadProfiles()
        profiles.clear()
        profiles.addAll(loaded)
        selectedId = ProfilesStore.getSelectedId()
    }

    LaunchedEffect(Unit) { reload() }

    // Add profile dialog state
    var showAdd by remember { mutableStateOf(false) }
    var addName by remember { mutableStateOf("") }
    var addDob by remember { mutableStateOf<Dob?>(null) }
    var showDobPicker by remember { mutableStateOf(false) }
    val picker = rememberDatePickerState()

    // Delete confirm state
    var showDelete by remember { mutableStateOf(false) }
    var toDelete by remember { mutableStateOf<UserProfile?>(null) }

    if (showDobPicker) {
        DatePickerDialog(
            onDismissRequest = { showDobPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = picker.selectedDateMillis
                    if (millis != null) addDob = epochMillisToDob(millis)
                    showDobPicker = false
                }) { Text("Done") }
            },
            dismissButton = { TextButton(onClick = { showDobPicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = picker) }
    }

    if (showAdd) {
        AlertDialog(
            onDismissRequest = { showAdd = false },
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
                    AssistChip(
                        onClick = { showDobPicker = true },
                        label = { Text(addDob?.format()?.let { "DOB: $it" } ?: "Select DOB") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val dob = addDob ?: return@TextButton
                    val created = ProfilesStore.addProfile(addName, dob)
                    ProfilesStore.setSelectedId(created.id)
                    addName = ""
                    addDob = null
                    showAdd = false
                    reload()
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAdd = false
                    addName = ""
                    addDob = null
                }) { Text("Cancel") }
            }
        )
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text("Delete profile?") },
            text = { Text("Delete “${toDelete?.name}”? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    toDelete?.let { ProfilesStore.deleteProfile(it.id) }
                    showDelete = false
                    toDelete = null
                    reload()
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDelete = false
                    toDelete = null
                }) { Text("Cancel") }
            }
        )
    }

    val selectedProfile = remember(profiles, selectedId) {
        profiles.firstOrNull { it.id == selectedId } ?: profiles.firstOrNull()
    }

    val hasBirthDetails = BirthDetailsStore.loadOrNull() != null

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        item {
            AboutHeaderCard(
                title = "AstroKit",
                subtitle = "Offline-first astrology + life math",
                tagline = "Cosmic insights with privacy built-in."
            )
        }

        item {
            QuickActionsCard(
                onRate = { rateApp() },
                onShare = {
                    val text = buildShareAppText()
                    shareText("AstroKit", text)
                },
                onExport = {
                    val export = buildExportText(selectedProfile)
                    copyToClipboard("AstroKit Profile", export)
                }
            )
        }

        item { SectionHeader("Profiles") }

        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    profiles.forEach { p ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        ProfilesStore.setSelectedId(p.id)
                                        selectedId = p.id
                                    }
                            ) {
                                Text(
                                    if (p.id == selectedId) "✓ ${p.name}" else p.name,
                                    fontWeight = if (p.id == selectedId) FontWeight.SemiBold else FontWeight.Normal
                                )
                                Text(p.dob.format(), style = MaterialTheme.typography.bodySmall)
                            }

                            if (profiles.size > 1) {
                                TextButton(onClick = {
                                    toDelete = p
                                    showDelete = true
                                }) { Text("Delete") }
                            }
                        }
                        HorizontalDivider()
                    }

                    Button(onClick = { showAdd = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Add profile")
                    }
                }
            }
        }

        item { SectionHeader("Kundli") }

        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

                    SettingsRow(
                        title = "Birth details (for Janam Kundli)",
                        subtitle = "Add time + place to generate your chart",
                        onClick = onOpenBirthDetails
                    )

                    SettingsRow(
                        title = "Open Kundli summary",
                        subtitle = if (hasBirthDetails) "Preview North Indian chart + houses + planets"
                        else "Add birth details first",
                        onClick = onOpenKundli
                    )
                }
            }
        }

        item { SectionHeader("Privacy & Legal") }

        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SettingsRow(
                        title = "Privacy Policy",
                        subtitle = "We’ll host this on GitHub Pages",
                        onClick = { openUrl("https://github.com/mobigaurav/AstroKit/blob/main/docs/privacy.md") } // swap later
                    )
                    SettingsRow(
                        title = "Terms of Service",
                        subtitle = "We’ll host this on GitHub Pages",
                        onClick = { openUrl("https://github.com/mobigaurav/AstroKit/blob/main/docs/terms.md") } // swap later
                    )
                }
            }
        }

        item { SectionHeader("Data") }

        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Profiles & insights are stored locally on this device.", style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = { InsightStore.clear() }) { Text("Clear saved insights") }
                }
            }
        }

        item { SectionHeader("About") }

        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val info = remember { getAppInfo() }

                    Text("Version", fontWeight = FontWeight.SemiBold)
                    Text("${info.versionName} (Build ${info.buildNumber})", style = MaterialTheme.typography.bodyMedium)

                    Text("Coming soon", fontWeight = FontWeight.SemiBold)
                    Text("• Moon sign • Rising sign • Vedic Kundli • Premium reports", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

/* ---------------------- Premium About Header ---------------------- */

@Composable
private fun AboutHeaderCard(
    title: String,
    subtitle: String,
    tagline: String
) {
    val cs = MaterialTheme.colorScheme
    val gradient = remember(cs) {
        androidx.compose.ui.graphics.Brush.linearGradient(
            listOf(
                cs.primary.copy(alpha = 0.90f),
                cs.secondary.copy(alpha = 0.70f),
                cs.tertiary.copy(alpha = 0.60f)
            )
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("✦ ✧ ✦", color = cs.onPrimary.copy(alpha = 0.85f))
                Text("✧ ✦ ✧", color = cs.onPrimary.copy(alpha = 0.75f))
            }

            Row(
                modifier = Modifier
                    .background(cs.onPrimary.copy(alpha = 0.18f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("✦", color = cs.onPrimary)
                Text("AstroKit", color = cs.onPrimary, fontWeight = FontWeight.ExtraBold)
                Text("✧", color = cs.onPrimary.copy(alpha = 0.9f))
            }

            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, color = cs.onPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = cs.onPrimary.copy(alpha = 0.92f))
            Text(tagline, style = MaterialTheme.typography.bodySmall, color = cs.onPrimary.copy(alpha = 0.88f))
        }
    }
}

/* ---------------------- Quick Actions ---------------------- */

@Composable
private fun QuickActionsCard(
    onRate: () -> Unit,
    onShare: () -> Unit,
    onExport: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Quick actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                FilledTonalButton(onClick = onRate, modifier = Modifier.weight(1f)) { Text("Rate app") }
                Button(onClick = onShare, modifier = Modifier.weight(1f)) { Text("Share app") }
            }

            FilledTonalButton(onClick = onExport, modifier = Modifier.fillMaxWidth()) {
                Text("Export profile (copy)")
            }
        }
    }
}

/* ---------------------- Settings rows ---------------------- */

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(title, fontWeight = FontWeight.SemiBold)
        Text(subtitle, style = MaterialTheme.typography.bodySmall)
        HorizontalDivider()
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
}

/* ---------------------- Export / Share text builders ---------------------- */

private fun buildExportText(p: UserProfile?): String {
    if (p == null) return "AstroKit profile: (no profile selected)"

    val computed = com.gaurav.astrokit.AstroKitEngine.computeProfile(
        year = p.dob.year,
        month = p.dob.month,
        day = p.dob.day
    )

    return """
AstroKit — Profile Export

Name: ${p.name}
DOB: ${p.dob.format()}

Computed (offline):
• Zodiac (Sun sign): ${computed.zodiac.displayName}
• Element: ${computed.zodiac.element}
• Modality: ${computed.zodiac.modality}
• Ruling planet: ${computed.zodiac.rulingPlanet}
• Life Path: ${computed.lifePath}
• Personal Year: ${computed.personalYear}

Notes:
• Zodiac is calculated using DOB-based Sun sign (Western/tropical).
• Kundli preview uses deterministic offline generation in Phase-2 (not ephemeris).
""".trimIndent()
}

private fun buildShareAppText(): String {
    return """
Try AstroKit ✨
Offline-first Zodiac + Life Path + Personal Year + Compatibility + Kundli preview.

Download & explore: (link will be added once published)
""".trimIndent()
}
