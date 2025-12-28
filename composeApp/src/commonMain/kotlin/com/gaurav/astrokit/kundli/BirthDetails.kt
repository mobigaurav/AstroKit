package com.gaurav.astrokit.kundli

import com.gaurav.astrokit.ui.Dob
import com.gaurav.astrokit.ui.format

data class BirthTime(val hour: Int, val minute: Int) {
    fun format(): String = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}

data class BirthDetails(
    val dob: Dob,
    val time: BirthTime,
    val place: String
) {
    fun summary(): String = "${dob.format()} • ${time.format()} • ${place.trim()}"
}
