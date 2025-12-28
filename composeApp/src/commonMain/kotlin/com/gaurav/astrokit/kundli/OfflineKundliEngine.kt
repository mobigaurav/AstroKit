package com.gaurav.astrokit.kundli

object OfflineKundliEngine {

    private val rashis = listOf(
        "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
        "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces"
    )

    // 27 Nakshatras (standard names)
    private val nakshatras = listOf(
        "Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashirsha", "Ardra",
        "Punarvasu", "Pushya", "Ashlesha", "Magha", "Purva Phalguni", "Uttara Phalguni",
        "Hasta", "Chitra", "Swati", "Vishakha", "Anuradha", "Jyeshtha",
        "Mula", "Purva Ashadha", "Uttara Ashadha", "Shravana", "Dhanishta", "Shatabhisha",
        "Purva Bhadrapada", "Uttara Bhadrapada", "Revati"
    )

    // Use “everything we may show” (your UI handles extra planets gracefully)
    private val planets = listOf(
        "Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn",
        "Rahu", "Ketu",
        "Uranus", "Neptune", "Pluto"
    )

    fun generate(details: BirthDetails): KundliChart {
        val baseSeed = stableSeed(details)

        val lagnaIndex = positiveMod(fnv1a32("lagna|$baseSeed"), 12)
        val lagna = rashis[lagnaIndex]

        // House -> sign (rotate from Lagna)
        val houseSign = (1..12).associateWith { h ->
            rashis[(lagnaIndex + (h - 1)) % 12]
        }

        val placedPlanets = planets.map { p ->
            val house = 1 + positiveMod(fnv1a32("planetHouse|$baseSeed|p=$p"), 12)
            PlanetInfo(
                planet = p,
                sign = houseSign.getValue(house),
                house = house
            )
        }

        val planetsByHouse: Map<Int, List<String>> =
            placedPlanets.groupBy { it.house }.mapValues { (_, v) -> v.map { it.planet } }

        val houses = (1..12).map { h ->
            HouseInfo(
                house = h,
                sign = houseSign.getValue(h),
                planets = planetsByHouse[h].orEmpty()
            )
        }

        // ✅ Required by your KundliChart model
        val moon = placedPlanets.firstOrNull { it.planet.equals("Moon", ignoreCase = true) }
        val moonSign = moon?.sign ?: rashis[positiveMod(fnv1a32("moonSign|$baseSeed"), 12)]
        val nakshatra = nakshatras[positiveMod(fnv1a32("nakshatra|$baseSeed|moon=$moonSign"), nakshatras.size)]

        return KundliChart(
            lagna = lagna,
            moonSign = moonSign,
            nakshatra = nakshatra,
            houses = houses,
            planets = placedPlanets
        )
    }

    private fun stableSeed(details: BirthDetails): String {
        val d = details.dob
        val t = details.time
        val place = details.place.trim().lowercase()
        return "y=${d.year}|m=${d.month}|d=${d.day}|hh=${t.hour}|mm=${t.minute}|place=$place"
    }

    /**
     * Stable hash across KMP targets: FNV-1a 32-bit
     */
    private fun fnv1a32(input: String): Int {
        var hash = 0x811C9DC5.toInt()
        val prime = 0x01000193
        for (ch in input) {
            hash = hash xor ch.code
            hash *= prime
        }
        return hash
    }

    private fun positiveMod(value: Int, mod: Int): Int {
        val r = value % mod
        return if (r < 0) r + mod else r
    }
}
