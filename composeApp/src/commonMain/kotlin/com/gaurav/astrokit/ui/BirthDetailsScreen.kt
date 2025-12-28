package com.gaurav.astrokit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.gaurav.astrokit.kundli.BirthDetails
import com.gaurav.astrokit.kundli.BirthTime
import com.gaurav.astrokit.storage.BirthDetailsStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthDetailsScreen(
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onSaved: () -> Unit
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

    // NOTE: keep "existing" stable for initial render, but show "Saved" chip using current store.
    val existing = remember { BirthDetailsStore.loadOrNull() }

    var dob by remember { mutableStateOf(existing?.dob) }
    var hour by remember { mutableStateOf(existing?.time?.hour?.toString() ?: "12") }
    var minute by remember { mutableStateOf(existing?.time?.minute?.toString() ?: "00") }
    var place by remember { mutableStateOf(existing?.place ?: "") }

    var showDobDialog by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    if (showDobDialog) {
        DobPickerDialog(
            initial = dob ?: Dob(1990, 1, 1),
            onDismiss = { showDobDialog = false },
            onPicked = {
                dob = it
                showDobDialog = false
            }
        )
    }

    fun validateAndSave() {
        error = null

        val d = dob ?: run {
            error = "Please select your date of birth."
            return
        }

        val hh = hour.toIntOrNull()
        val mm = minute.toIntOrNull()
        if (hh == null || hh !in 0..23) {
            error = "Hour must be between 0 and 23."
            return
        }
        if (mm == null || mm !in 0..59) {
            error = "Minute must be between 0 and 59."
            return
        }
        if (place.trim().isBlank()) {
            error = "Please enter your place of birth."
            return
        }

        BirthDetailsStore.save(
            BirthDetails(
                dob = d,
                time = BirthTime(hh, mm),
                place = place.trim()
            )
        )
        onSaved()
    }

    fun clearAll() {
        BirthDetailsStore.clear()
        dob = null
        hour = "12"
        minute = "00"
        place = ""
        error = null
    }

    // ✅ The key fix:
    // - Scaffold bottomBar keeps Save/Clear always visible
    // - navigationBarsPadding + imePadding handles iOS/Android safe areas + keyboard
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Surface(tonalElevation = 6.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (error != null) {
                        Text(error!!, color = cs.error, style = MaterialTheme.typography.bodySmall)
                    }

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { validateAndSave() },
                            modifier = Modifier.weight(1f)
                        ) { Text("Save") }

                        OutlinedButton(
                            onClick = { clearAll() },
                            modifier = Modifier.weight(1f)
                        ) { Text("Clear") }
                    }
                }
            }
        }
    ) { innerPadding ->
        // ✅ Scrollable content area
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
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
                        Text(
                            "Birth Details",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = cs.onPrimary
                        )
                        Text(
                            "These are used to generate your Kundli preview. Accurate Kundli will later use real planetary degrees + location coordinates.",
                            style = MaterialTheme.typography.bodySmall,
                            color = cs.onPrimary.copy(alpha = 0.90f)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FilledTonalButton(onClick = onBack) { Text("Back") }
                            val isSaved = BirthDetailsStore.loadOrNull() != null
                            AssistChip(
                                onClick = { },
                                label = { Text(if (isSaved) "Saved" else "Not saved") }
                            )
                        }
                    }
                }
            }

            item {
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                        Text("Date of Birth", fontWeight = FontWeight.SemiBold)

                        OutlinedButton(
                            onClick = { showDobDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(dob?.format() ?: "Select DOB")
                        }

                        HorizontalDivider()

                        Text("Time of Birth", fontWeight = FontWeight.SemiBold)

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = hour,
                                onValueChange = { hour = it.filter(Char::isDigit).take(2) },
                                label = { Text("Hour (0-23)") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions.Default
                            )
                            OutlinedTextField(
                                value = minute,
                                onValueChange = { minute = it.filter(Char::isDigit).take(2) },
                                label = { Text("Minute (0-59)") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions.Default
                            )
                        }

                        HorizontalDivider()

                        Text("Place of Birth", fontWeight = FontWeight.SemiBold)

                        OutlinedTextField(
                            value = place,
                            onValueChange = { place = it },
                            label = { Text("City, Country") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words
                            )
                        )
                    }
                }
            }

//            item {
//                Card(colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant.copy(alpha = 0.35f))) {
//                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
//                        Text("Preview note", fontWeight = FontWeight.SemiBold)
//                        Text(
//                            "Phase-2.2B uses a deterministic offline preview engine: same birth details → same chart preview. " +
//                                    "Phase-3 will compute exact degrees for a real Kundli.",
//                            style = MaterialTheme.typography.bodySmall
//                        )
//                    }
//                }
//            }

            // ✅ Spacer so last content never sits behind sticky bottom bar
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

/**
 * KMP-safe DOB picker dialog (no Android DatePicker dependency).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DobPickerDialog(
    initial: Dob,
    onDismiss: () -> Unit,
    onPicked: (Dob) -> Unit
) {
    var year by remember { mutableStateOf(initial.year) }
    var month by remember { mutableStateOf(initial.month) }
    var day by remember { mutableStateOf(initial.day) }

    val years = remember { (1950..2035).toList() }
    val months = remember { (1..12).toList() }
    val days = remember { (1..31).toList() }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onPicked(Dob(year, month, day)) }) { Text("Done") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Select Date of Birth") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IntDropdown(
                        label = "Year",
                        values = years,
                        selected = year,
                        onSelected = { year = it },
                        modifier = Modifier.weight(1f)
                    )
                    IntDropdown(
                        label = "Month",
                        values = months,
                        selected = month,
                        onSelected = { month = it },
                        modifier = Modifier.weight(1f)
                    )
                }
                IntDropdown(
                    label = "Day",
                    values = days,
                    selected = day,
                    onSelected = { day = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "Selected: ${Dob(year, month, day).format()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IntDropdown(
    label: String,
    values: List<Int>,
    selected: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            values.forEach { v ->
                DropdownMenuItem(
                    text = { Text(v.toString()) },
                    onClick = {
                        onSelected(v)
                        expanded = false
                    }
                )
            }
        }
    }
}
