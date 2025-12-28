package com.gaurav.astrokit.ui

data class Dob(val year: Int, val month: Int, val day: Int)

/**
 * Convert epoch millis (DatePicker gives UTC millis at start-of-day) to YYYY-MM-DD.
 * This implementation is pure Kotlin and works on Android + iOS (Kotlin/Native).
 *
 * Based on a standard civil_from_days algorithm (no java.time, no kotlinx-datetime).
 */
fun epochMillisToDob(epochMillis: Long): Dob {
    val daysSinceEpoch = floorDiv(epochMillis, MILLIS_PER_DAY)
    return civilFromDays(daysSinceEpoch)
}

fun Dob.format(): String =
    year.toString().padStart(4, '0') + "-" +
            month.toString().padStart(2, '0') + "-" +
            day.toString().padStart(2, '0')

private const val MILLIS_PER_DAY = 86_400_000L

private fun floorDiv(x: Long, y: Long): Long {
    var r = x / y
    if ((x xor y) < 0 && r * y != x) r -= 1
    return r
}

/**
 * Convert days since 1970-01-01 to civil date (year-month-day) in proleptic Gregorian calendar.
 */
private fun civilFromDays(daysSinceEpoch: Long): Dob {
    // Shift to civil day count used by the algorithm
    var z = daysSinceEpoch + 719468L
    val era = floorDiv(z, 146097L)
    val doe = z - era * 146097L                     // [0, 146096]
    val yoe = (doe - doe / 1460L + doe / 36524L - doe / 146096L) / 365L  // [0, 399]
    var y = (yoe + era * 400L).toInt()
    val doy = (doe - (365L * yoe + yoe / 4L - yoe / 100L))              // [0, 365]
    val mp = (5L * doy + 2L) / 153L                                     // [0, 11]
    val d = (doy - (153L * mp + 2L) / 5L + 1L).toInt()                  // [1, 31]
    var m = (mp + if (mp < 10) 3 else -9).toInt()                       // [1, 12]
    y += if (m <= 2) 1 else 0

    return Dob(year = y, month = m, day = d)
}
