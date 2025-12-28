package com.gaurav.astrokit

import com.gaurav.astrokit.astro.ZodiacCalculator
import com.gaurav.astrokit.astro.ZodiacSign
import com.gaurav.astrokit.lifemath.Numerology
import com.gaurav.astrokit.platform.currentYear

data class AstroProfile(
    val zodiac: ZodiacSign,
    val lifePath: Int,
    val personalYear: Int
)

object AstroKitEngine {
    fun computeProfile(year: Int, month: Int, day: Int): AstroProfile {
        val zodiac = ZodiacCalculator.signFor(month, day)
        val lifePath = Numerology.lifePath(year, month, day)
        val cy = currentYear()
        val personalYear = Numerology.personalYear(month, day, cy)

        return AstroProfile(
            zodiac = zodiac,
            lifePath = lifePath,
            personalYear = personalYear
        )
    }
}
