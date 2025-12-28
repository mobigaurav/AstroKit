package com.gaurav.astrokit.kundli

/**
 * Phase-2.2 will call AWS backend.
 * For Phase-2.1 we use a mock so UI is fully buildable + testable.
 */
interface KundliService {
    suspend fun generateChart(input: BirthInput): KundliChart
}

class MockKundliService : KundliService {
    override suspend fun generateChart(input: BirthInput): KundliChart {
        // Deterministic mock (not random). Just to validate UI.
        return KundliChart(
            lagna = "Scorpio",
            moonSign = "Cancer",
            nakshatra = "Pushya",
            houses = (1..12).map { h ->
                HouseInfo(
                    house = h,
                    sign = when (h) {
                        1 -> "Scorpio"
                        2 -> "Sagittarius"
                        3 -> "Capricorn"
                        4 -> "Aquarius"
                        5 -> "Pisces"
                        6 -> "Aries"
                        7 -> "Taurus"
                        8 -> "Gemini"
                        9 -> "Cancer"
                        10 -> "Leo"
                        11 -> "Virgo"
                        else -> "Libra"
                    },
                    planets = when (h) {
                        1 -> listOf("Mars")
                        9 -> listOf("Moon")
                        12 -> listOf("Sun")
                        else -> emptyList()
                    }
                )
            },
            planets = listOf(
                PlanetInfo("Sun", "Libra", 12),
                PlanetInfo("Moon", "Cancer", 9),
                PlanetInfo("Mars", "Scorpio", 1),
                PlanetInfo("Mercury", "Libra", 12),
                PlanetInfo("Jupiter", "Cancer", 9),
                PlanetInfo("Venus", "Virgo", 11),
                PlanetInfo("Saturn", "Capricorn", 3),
                PlanetInfo("Rahu", "Pisces", 5),
                PlanetInfo("Ketu", "Virgo", 11)
            )
        )
    }
}
