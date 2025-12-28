package com.gaurav.astrokit.ui

import androidx.compose.ui.graphics.Color
import com.gaurav.astrokit.astro.Element

object ZodiacPalette {
    fun accentFor(element: Element): Color = when (element) {
        Element.FIRE -> Color(0xFFFF6B6B)
        Element.EARTH -> Color(0xFF2ECC71)
        Element.AIR -> Color(0xFF4D96FF)
        Element.WATER -> Color(0xFF6C5CE7)
    }
}
