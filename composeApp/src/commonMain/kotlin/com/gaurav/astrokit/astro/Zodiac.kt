package com.gaurav.astrokit.astro

enum class Element { FIRE, EARTH, AIR, WATER }
enum class Modality { CARDINAL, FIXED, MUTABLE }
enum class Planet { MARS, VENUS, MERCURY, MOON, SUN, JUPITER, SATURN, URANUS, NEPTUNE, PLUTO }

enum class ZodiacSign(
    val displayName: String,
    val element: Element,
    val modality: Modality,
    val rulingPlanet: Planet
) {
    ARIES("Aries", Element.FIRE, Modality.CARDINAL, Planet.MARS),
    TAURUS("Taurus", Element.EARTH, Modality.FIXED, Planet.VENUS),
    GEMINI("Gemini", Element.AIR, Modality.MUTABLE, Planet.MERCURY),
    CANCER("Cancer", Element.WATER, Modality.CARDINAL, Planet.MOON),
    LEO("Leo", Element.FIRE, Modality.FIXED, Planet.SUN),
    VIRGO("Virgo", Element.EARTH, Modality.MUTABLE, Planet.MERCURY),
    LIBRA("Libra", Element.AIR, Modality.CARDINAL, Planet.VENUS),
    SCORPIO("Scorpio", Element.WATER, Modality.FIXED, Planet.PLUTO),
    SAGITTARIUS("Sagittarius", Element.FIRE, Modality.MUTABLE, Planet.JUPITER),
    CAPRICORN("Capricorn", Element.EARTH, Modality.CARDINAL, Planet.SATURN),
    AQUARIUS("Aquarius", Element.AIR, Modality.FIXED, Planet.URANUS),
    PISCES("Pisces", Element.WATER, Modality.MUTABLE, Planet.NEPTUNE)
}

object ZodiacCalculator {
    /**
     * Month: 1..12, Day: 1..31 (basic validation assumed by UI)
     */
    fun signFor(month: Int, day: Int): ZodiacSign = when (month) {
        1 -> if (day <= 19) ZodiacSign.CAPRICORN else ZodiacSign.AQUARIUS
        2 -> if (day <= 18) ZodiacSign.AQUARIUS else ZodiacSign.PISCES
        3 -> if (day <= 20) ZodiacSign.PISCES else ZodiacSign.ARIES
        4 -> if (day <= 19) ZodiacSign.ARIES else ZodiacSign.TAURUS
        5 -> if (day <= 20) ZodiacSign.TAURUS else ZodiacSign.GEMINI
        6 -> if (day <= 20) ZodiacSign.GEMINI else ZodiacSign.CANCER
        7 -> if (day <= 22) ZodiacSign.CANCER else ZodiacSign.LEO
        8 -> if (day <= 22) ZodiacSign.LEO else ZodiacSign.VIRGO
        9 -> if (day <= 22) ZodiacSign.VIRGO else ZodiacSign.LIBRA
        10 -> if (day <= 22) ZodiacSign.LIBRA else ZodiacSign.SCORPIO
        11 -> if (day <= 21) ZodiacSign.SCORPIO else ZodiacSign.SAGITTARIUS
        12 -> if (day <= 21) ZodiacSign.SAGITTARIUS else ZodiacSign.CAPRICORN
        else -> ZodiacSign.ARIES
    }
}
