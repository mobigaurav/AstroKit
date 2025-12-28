package com.gaurav.astrokit.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.gaurav.astrokit.kundli.HouseInfo
import com.gaurav.astrokit.kundli.HouseMeaningCatalog
import com.gaurav.astrokit.kundli.KundliChart
import com.gaurav.astrokit.kundli.PlanetInfo
import com.gaurav.astrokit.kundli.PlanetMeaningCatalog
import com.gaurav.astrokit.storage.BirthDetailsStore
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KundliSummaryScreen(
    contentPadding: PaddingValues,
    chart: KundliChart,
    onBack: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val headerBrush = remember(cs) {
        Brush.linearGradient(
            listOf(
                cs.primary.copy(alpha = 0.90f),
                cs.secondary.copy(alpha = 0.70f),
                cs.tertiary.copy(alpha = 0.60f)
            )
        )
    }

    // Birth details (stored locally) — shown as summary; may be null
    val birthDetails = remember { BirthDetailsStore.loadOrNull() }

    val lagnaSign = remember(chart) { chart.lagna.trim() }
    val housesByNumber = remember(chart) { chart.houses.associateBy { it.house } }
    val planetsByHouse = remember(chart) { chart.planets.groupBy { it.house } }

    var selectedHouse by remember { mutableStateOf<Int?>(null) }
    var selectedPlanet by remember { mutableStateOf<PlanetInfo?>(null) }
    var showHouseSheet by remember { mutableStateOf(false) }
    var showPlanetSheet by remember { mutableStateOf(false) }

    // Clear confirm dialog
    var showClearConfirm by remember { mutableStateOf(false) }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear birth details?") },
            text = { Text("This removes saved DOB/time/place. Your profile list stays intact.") },
            confirmButton = {
                TextButton(onClick = {
                    BirthDetailsStore.clear()
                    showClearConfirm = false
                    onBack() // return to previous screen (AppRoot sends back to Settings)
                }) { Text("Clear") }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) { Text("Cancel") }
            }
        )
    }

    if (showHouseSheet) {
        val h = selectedHouse ?: 1
        val info = housesByNumber[h]
        HouseDetailsSheet(
            house = h,
            info = info,
            lagnaSign = lagnaSign,
            onDismiss = { showHouseSheet = false }
        )
    }

    if (showPlanetSheet) {
        val p = selectedPlanet
        if (p != null) {
            PlanetDetailsSheet(
                planet = p,
                lagnaSign = lagnaSign,
                onDismiss = { showPlanetSheet = false }
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(headerBrush)
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("✦ ✧ ✦", color = cs.onPrimary.copy(alpha = 0.85f))
                        Text("✧ ✦ ✧", color = cs.onPrimary.copy(alpha = 0.75f))
                    }

                    Text(
                        "Kundli",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = cs.onPrimary
                    )
                    Text(
                        "North Indian chart preview. Lagna house pulses. Tap any house for details.",
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.onPrimary.copy(alpha = 0.90f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalButton(onClick = onBack) { Text("Back") }
                        AssistChip(onClick = {}, label = { Text("Offline Preview") })
                    }
                }
            }
        }

        // ✅ Birth details summary + actions
        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Birth details", fontWeight = FontWeight.SemiBold)

                    if (birthDetails == null) {
                        Text(
                            "No saved birth details found. Add DOB/time/place from Settings → Birth details.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        InfoRow("DOB", birthDetails.dob.format())
                        InfoRow(
                            "Time",
                            birthDetails.time.hour.toString().padStart(2, '0') + ":" +
                                    birthDetails.time.minute.toString().padStart(2, '0')
                        )
                        InfoRow("Place", birthDetails.place)
                    }

                    HorizontalDivider()

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        FilledTonalButton(
                            onClick = {
                                // In our navigation, "Back" goes to Settings. Birth details is inside Settings.
                                onBack()
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Edit") }

                        OutlinedButton(
                            onClick = { showClearConfirm = true },
                            modifier = Modifier.weight(1f),
                            enabled = birthDetails != null
                        ) { Text("Clear") }
                    }
                }
            }
        }

        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Planet symbols", fontWeight = FontWeight.SemiBold)
                    PlanetLegendRow()
                }
            }
        }

        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Janam Kundli (North Indian)", fontWeight = FontWeight.SemiBold)
                    Text(
                        "Chart is kept clean: only house labels + icons. Full details appear on tap.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    NorthIndianKundliChart(
                        housesByNumber = housesByNumber,
                        lagnaSign = lagnaSign,
                        modifier = Modifier.fillMaxWidth(),
                        onHouseClick = { h ->
                            selectedHouse = h
                            showHouseSheet = true
                        }
                    )
                }
            }
        }

        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Planet placements", fontWeight = FontWeight.SemiBold)
                    Text(
                        "Grouped by house. Tap a planet for details.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Houses 1..12, show only if there are planets
        (1..12).forEach { house ->
            val planets = planetsByHouse[house].orEmpty()
            if (planets.isNotEmpty()) {
                item {
                    HouseSectionHeader(house = house, sign = housesByNumber[house]?.sign)
                }
                items(planets) { p ->
                    PlanetRow(
                        planet = p,
                        onClick = {
                            selectedPlanet = p
                            showPlanetSheet = true
                        }
                    )
                }
            }
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

/**
 * IMPORTANT:
 * We are using the anchor values you provided (device-tested).
 */
@Composable
private fun NorthIndianKundliChart(
    housesByNumber: Map<Int, HouseInfo>,
    lagnaSign: String,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 2.dp,
    onHouseClick: (Int) -> Unit
) {
    val cs = MaterialTheme.colorScheme

    val anchors: List<HouseAnchor> = remember {
        listOf(
            // Top band
            HouseAnchor(1, 0.52f, 0.20f),   // top-center
            HouseAnchor(2, 0.22f, 0.08f),   // top slightly-left

            // Top corners / upper sides
            HouseAnchor(3, 0.11f, 0.22f),   // top-left corner
            HouseAnchor(12, 0.76f, 0.08f),  // top-right corner
            HouseAnchor(11, 0.90f, 0.22f),  // right-upper

            // Middle band
            HouseAnchor(4, 0.26f, 0.46f),   // left-middle
            HouseAnchor(10, 0.74f, 0.46f),  // right-middle

            // Bottom band
            HouseAnchor(5, 0.11f, 0.70f),   // left-lower
            HouseAnchor(6, 0.24f, 0.89f),   // bottom-left corner
            HouseAnchor(7, 0.50f, 0.78f),   // bottom-center
            HouseAnchor(8, 0.72f, 0.89f),   // bottom slightly-right
            HouseAnchor(9, 0.90f, 0.78f)    // bottom-right corner
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(cs.surfaceVariant.copy(alpha = 0.35f))
            .padding(10.dp)
    ) {
        val w = constraints.maxWidth
        val h = constraints.maxHeight
        val minSide = min(w, h)

        // Keep cards consistent and not huge.
        val hotspotW = (minSide * 0.16f).toInt().coerceIn(68, 126)
        val hotspotH = (minSide * 0.11f).toInt().coerceIn(50, 92)

        Canvas(modifier = Modifier.fillMaxSize()) {
            val sw = strokeWidth.toPx()
            val wf = size.width
            val hf = size.height

            // Outer square
            drawRect(color = cs.onSurface.copy(alpha = 0.70f), style = Stroke(width = sw))

            // Inner diamond (midpoints)
            val top = Offset(wf * 0.5f, 0f)
            val right = Offset(wf, hf * 0.5f)
            val bottom = Offset(wf * 0.5f, hf)
            val left = Offset(0f, hf * 0.5f)

            val diamond = Path().apply {
                moveTo(top.x, top.y)
                lineTo(right.x, right.y)
                lineTo(bottom.x, bottom.y)
                lineTo(left.x, left.y)
                close()
            }
            drawPath(diamond, color = cs.onSurface.copy(alpha = 0.70f), style = Stroke(width = sw))

            // Diagonal X (kept because your reference image uses it)
            drawLine(cs.onSurface.copy(alpha = 0.55f), Offset(0f, 0f), Offset(wf, hf), sw)
            drawLine(cs.onSurface.copy(alpha = 0.55f), Offset(wf, 0f), Offset(0f, hf), sw)
        }

        anchors.forEach { anchor ->
            val info = housesByNumber[anchor.house]
            val isLagnaHouse = isSameSign(info?.sign, lagnaSign)
            val hasPlanets = info?.planets?.isNotEmpty() == true

            val xPx = (w * anchor.fx).toInt()
            val yPx = (h * anchor.fy).toInt()

            Box(
                modifier = Modifier
                    .offset { IntOffset(xPx - hotspotW / 2, yPx - hotspotH / 2) }
                    .size(hotspotW.pxToDp(), hotspotH.pxToDp())
                    .clickable { onHouseClick(anchor.house) },
                contentAlignment = Alignment.Center
            ) {
                HouseMiniCard(
                    house = anchor.house,
                    isLagna = isLagnaHouse,
                    hasPlanets = hasPlanets,
                    onClick = { onHouseClick(anchor.house) }
                )
            }
        }
    }
}

private data class HouseAnchor(val house: Int, val fx: Float, val fy: Float)

@Composable
private fun HouseMiniCard(
    house: Int,
    isLagna: Boolean,
    hasPlanets: Boolean,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme

    val infinite = rememberInfiniteTransition()
    val pulseAlpha by infinite.animateFloat(
        initialValue = 0.30f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val borderColor = if (isLagna) cs.primary.copy(alpha = pulseAlpha) else cs.outline.copy(alpha = 0.22f)
    val bg = if (isLagna) cs.primaryContainer.copy(alpha = 0.92f) else cs.surface

    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = if (isLagna) 6.dp else 1.dp,
        color = bg,
        modifier = Modifier
            .border(2.dp, borderColor, MaterialTheme.shapes.large)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text("H$house", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge)
            when {
                isLagna -> Text("⭐", style = MaterialTheme.typography.labelLarge)
                hasPlanets -> Text("🪐", style = MaterialTheme.typography.labelLarge)
                else -> Text(" ", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun PlanetLegendRow() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Legend(PlanetMeaningCatalog.symbolOf("Sun"), "Sun")
            Legend(PlanetMeaningCatalog.symbolOf("Moon"), "Moon")
            Legend(PlanetMeaningCatalog.symbolOf("Mars"), "Mars")
            Legend(PlanetMeaningCatalog.symbolOf("Mercury"), "Mercury")
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Legend(PlanetMeaningCatalog.symbolOf("Venus"), "Venus")
            Legend(PlanetMeaningCatalog.symbolOf("Jupiter"), "Jupiter")
            Legend(PlanetMeaningCatalog.symbolOf("Saturn"), "Saturn")
            Legend(PlanetMeaningCatalog.symbolOf("Rahu"), "Rahu")
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Legend(PlanetMeaningCatalog.symbolOf("Ketu"), "Ketu")
            Legend(PlanetMeaningCatalog.symbolOf("Uranus"), "Uranus")
            Legend(PlanetMeaningCatalog.symbolOf("Neptune"), "Neptune")
            Legend(PlanetMeaningCatalog.symbolOf("Pluto"), "Pluto")
        }
    }
}

@Composable
private fun Legend(sym: String, name: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(sym, style = MaterialTheme.typography.titleMedium)
        Text(name, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun HouseSectionHeader(house: Int, sign: String?) {
    val cs = MaterialTheme.colorScheme
    val meaning = remember(house) { HouseMeaningCatalog.meaning(house) }

    Card(
        colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant.copy(alpha = 0.45f))
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("H$house • ${meaning.title}", fontWeight = FontWeight.SemiBold)
                AssistChip(onClick = {}, label = { Text(sign ?: "—") })
            }
            Text(meaning.themes, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun PlanetRow(
    planet: PlanetInfo,
    onClick: () -> Unit
) {
    val symbol = PlanetMeaningCatalog.symbolOf(planet.planet)
    Card(
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("$symbol  ${planet.planet}", fontWeight = FontWeight.SemiBold)
                Text("House ${planet.house} • ${planet.sign}", style = MaterialTheme.typography.bodySmall)
            }
            AssistChip(onClick = {}, label = { Text("H${planet.house}") })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HouseDetailsSheet(
    house: Int,
    info: HouseInfo?,
    lagnaSign: String,
    onDismiss: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val sign = info?.sign ?: "—"
    val planets = info?.planets ?: emptyList()
    val symbols = remember(planets) {
        if (planets.isEmpty()) "—"
        else planets.joinToString(" ") { PlanetMeaningCatalog.symbolOf(it) }
    }
    val isLagnaHouse = isSameSign(sign, lagnaSign)
    val meaning = remember(house) { HouseMeaningCatalog.meaning(house) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("House $house", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        if (isLagnaHouse) AssistChip(onClick = {}, label = { Text("Lagna") })
                    }
                    Text("${meaning.title} • ${meaning.keywords.joinToString(" • ")}", style = MaterialTheme.typography.bodySmall)
                }
                AssistChip(onClick = onDismiss, label = { Text("Close") })
            }

            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    InfoRow("Sign", sign)
                    InfoRow("Planets", if (planets.isEmpty()) "None" else planets.joinToString(", "))
                    InfoRow("Symbols", symbols)
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant.copy(alpha = 0.35f))) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Key effects (offline)", fontWeight = FontWeight.SemiBold)
                    Text(meaning.themes, style = MaterialTheme.typography.bodySmall)
                }
            }

            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Explain this house (AI)", fontWeight = FontWeight.SemiBold)
                    Text(
                        "Premium feature — will be enabled once we calculate accurate planetary degrees from birth time + place.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    FilledTonalButton(
                        onClick = { /* later */ },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Generate AI explanation") }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanetDetailsSheet(
    planet: PlanetInfo,
    lagnaSign: String,
    onDismiss: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val meaning = remember(planet.planet) { PlanetMeaningCatalog.meaning(planet.planet) }
    val houseMeaning = remember(planet.house) { HouseMeaningCatalog.meaning(planet.house) }
    val isLagnaSign = isSameSign(planet.sign, lagnaSign)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "${meaning.symbol}  ${planet.planet}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "House ${planet.house} • ${planet.sign}${if (isLagnaSign) " • Lagna Sign" else ""}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                AssistChip(onClick = onDismiss, label = { Text("Close") })
            }

            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("What it represents", fontWeight = FontWeight.SemiBold)
                    Text(meaning.short, style = MaterialTheme.typography.bodySmall)
                    Text("Themes: ${meaning.themes.joinToString(" • ")}", style = MaterialTheme.typography.bodySmall)
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant.copy(alpha = 0.35f))) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("In this house", fontWeight = FontWeight.SemiBold)
                    Text(
                        "House ${planet.house} (${houseMeaning.title}) relates to: ${houseMeaning.keywords.joinToString(", ")}.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("AI interpretation (stub)", fontWeight = FontWeight.SemiBold)
                    Text(
                        "Premium feature — will generate a full reading once birth-time/place based degrees are computed (Phase-3).",
                        style = MaterialTheme.typography.bodySmall
                    )
                    FilledTonalButton(
                        onClick = { /* later */ },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Generate AI reading") }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

private fun isSameSign(a: String?, b: String?): Boolean {
    if (a == null || b == null) return false
    return a.trim().equals(b.trim(), ignoreCase = true)
}

@Composable
private fun Int.pxToDp(): Dp {
    val density = androidx.compose.ui.platform.LocalDensity.current
    return with(density) { this@pxToDp.toDp() }
}
