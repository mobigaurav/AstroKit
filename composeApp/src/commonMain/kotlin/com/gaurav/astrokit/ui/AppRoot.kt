package com.gaurav.astrokit.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import com.gaurav.astrokit.kundli.KundliChart
import com.gaurav.astrokit.kundli.OfflineKundliEngine
import com.gaurav.astrokit.storage.BirthDetailsStore

private enum class AppScreen { HOME, SETTINGS, BIRTH_DETAILS, KUNDLI_SUMMARY }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    var screen by remember { mutableStateOf(AppScreen.HOME) }

    // Keep it, but don't depend on it for correctness
    var lastChart by remember { mutableStateOf<KundliChart?>(null) }

    fun goToKundliSummary() {
        val details = BirthDetailsStore.loadOrNull()
        if (details != null) {
            lastChart = OfflineKundliEngine.generate(details)
            screen = AppScreen.KUNDLI_SUMMARY
        } else {
            // No saved birth details => go to Birth Details
            screen = AppScreen.BIRTH_DETAILS
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        when (screen) {
                            AppScreen.HOME -> "AstroKit"
                            AppScreen.SETTINGS -> "Settings"
                            AppScreen.BIRTH_DETAILS -> "Birth details"
                            AppScreen.KUNDLI_SUMMARY -> "Kundli"
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    if (screen != AppScreen.HOME) {
                        IconButton(onClick = {
                            screen = when (screen) {
                                AppScreen.SETTINGS -> AppScreen.HOME
                                AppScreen.BIRTH_DETAILS -> AppScreen.SETTINGS
                                AppScreen.KUNDLI_SUMMARY -> AppScreen.SETTINGS // ✅ Kundli back → Settings
                                else -> AppScreen.HOME
                            }
                        }) { Text("←") }
                    }
                },
                actions = {
                    if (screen == AppScreen.HOME) {
                        IconButton(onClick = { screen = AppScreen.SETTINGS }) { Text("⚙️") }
                    }
                }
            )
        }
    ) { padding ->
        when (screen) {
            AppScreen.HOME -> AstroHomeScreen(contentPadding = padding)

            AppScreen.SETTINGS -> SettingsScreen(
                contentPadding = padding,
                onOpenBirthDetails = { screen = AppScreen.BIRTH_DETAILS },
                // ✅ If you already have a Kundli entry in Settings, use this callback.
                // If your SettingsScreen doesn't yet have it, just ignore this parameter and remove it.
                onOpenKundli = { goToKundliSummary() }
            )

            AppScreen.BIRTH_DETAILS -> BirthDetailsScreen(
                contentPadding = padding,
                onBack = { screen = AppScreen.SETTINGS },
                onSaved = { goToKundliSummary() } // ✅ Save → open Kundli
            )

            AppScreen.KUNDLI_SUMMARY -> {
                // ✅ Always recompute from saved details to survive app restarts
                val chart = remember(lastChart) {
                    lastChart ?: BirthDetailsStore.loadOrNull()?.let { OfflineKundliEngine.generate(it) }
                }

                if (chart == null) {
                    // No saved details, redirect to birth details
                    BirthDetailsScreen(
                        contentPadding = padding,
                        onBack = { screen = AppScreen.SETTINGS },
                        onSaved = { goToKundliSummary() }
                    )
                } else {
                    KundliSummaryScreen(
                        contentPadding = padding,
                        chart = chart,
                        onBack = { screen = AppScreen.SETTINGS }
                    )
                }
            }
        }
    }
}
