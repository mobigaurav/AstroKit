package com.gaurav.astrokit.kundli

/**
 * Birth input for deterministic chart calculation (backend in Phase-2.2).
 * We keep this minimal + stable for KMP.
 */
data class BirthInput(
    val date: SimpleDate,         // YYYY-MM-DD
    val time: SimpleTime,         // HH:MM (24h)
    val timezoneOffsetMinutes: Int, // e.g. +330 for IST
    val place: GeoPlace
)

data class SimpleDate(val year: Int, val month: Int, val day: Int) {
    fun format(): String =
        year.toString().padStart(4, '0') + "-" +
                month.toString().padStart(2, '0') + "-" +
                day.toString().padStart(2, '0')
}

data class SimpleTime(val hour: Int, val minute: Int) {
    fun format(): String =
        hour.toString().padStart(2, '0') + ":" +
                minute.toString().padStart(2, '0')
}

data class GeoPlace(
    val name: String,
    val lat: Double,
    val lon: Double
)

/**
 * Phase-2.2 output shape. We'll start by mocking this locally.
 * Later backend returns this JSON.
 */
data class KundliChart(
    val lagna: String,
    val moonSign: String,
    val nakshatra: String,
    val houses: List<HouseInfo>,
    val planets: List<PlanetInfo>
)

data class HouseInfo(
    val house: Int,          // 1..12
    val sign: String,        // Aries..Pisces
    val planets: List<String> // planet names
)

data class PlanetInfo(
    val planet: String,      // "Sun", "Moon", ...
    val sign: String,
    val house: Int
)
