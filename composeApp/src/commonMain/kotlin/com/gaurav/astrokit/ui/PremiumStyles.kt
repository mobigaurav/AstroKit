package com.gaurav.astrokit.ui

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.gaurav.astrokit.astro.Element

object PremiumStyles {

    fun elementGradient(element: Element): Brush {
        val colors = when (element) {
            Element.FIRE -> listOf(
                Color(0xFFFF6A00), // warm orange
                Color(0xFFFF2D55), // hot pink/red
                Color(0xFF7C3AED)  // violet
            )
            Element.EARTH -> listOf(
                Color(0xFF16A34A), // green
                Color(0xFF0EA5E9), // teal/sky
                Color(0xFF0F766E)  // deep teal
            )
            Element.AIR -> listOf(
                Color(0xFF38BDF8), // sky
                Color(0xFFA78BFA), // purple
                Color(0xFFFDE047)  // soft gold
            )
            Element.WATER -> listOf(
                Color(0xFF2563EB), // blue
                Color(0xFF06B6D4), // cyan
                Color(0xFF0F172A)  // deep navy
            )
        }
        return Brush.linearGradient(colors)
    }

    fun elementAccent(element: Element): Color {
        return when (element) {
            Element.FIRE -> Color(0xFFFF2D55)
            Element.EARTH -> Color(0xFF16A34A)
            Element.AIR -> Color(0xFF38BDF8)
            Element.WATER -> Color(0xFF2563EB)
        }
    }
}
