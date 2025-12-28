package com.gaurav.astrokit.storage

import com.gaurav.astrokit.core.AppDI
import com.gaurav.astrokit.kundli.BirthDetails
import com.gaurav.astrokit.kundli.BirthTime
import com.gaurav.astrokit.ui.Dob

private const val KEY_BIRTH_YEAR = "birth_year"
private const val KEY_BIRTH_MONTH = "birth_month"
private const val KEY_BIRTH_DAY = "birth_day"
private const val KEY_BIRTH_HOUR = "birth_hour"
private const val KEY_BIRTH_MINUTE = "birth_minute"
private const val KEY_BIRTH_PLACE = "birth_place"

object BirthDetailsStore {

    fun save(details: BirthDetails) {
        AppDI.settings.putInt(KEY_BIRTH_YEAR, details.dob.year)
        AppDI.settings.putInt(KEY_BIRTH_MONTH, details.dob.month)
        AppDI.settings.putInt(KEY_BIRTH_DAY, details.dob.day)
        AppDI.settings.putInt(KEY_BIRTH_HOUR, details.time.hour)
        AppDI.settings.putInt(KEY_BIRTH_MINUTE, details.time.minute)
        AppDI.settings.putString(KEY_BIRTH_PLACE, details.place.trim())
    }

    fun loadOrNull(): BirthDetails? {
        val y = AppDI.settings.getIntOrNull(KEY_BIRTH_YEAR) ?: return null
        val m = AppDI.settings.getIntOrNull(KEY_BIRTH_MONTH) ?: return null
        val d = AppDI.settings.getIntOrNull(KEY_BIRTH_DAY) ?: return null
        val hh = AppDI.settings.getIntOrNull(KEY_BIRTH_HOUR) ?: return null
        val mm = AppDI.settings.getIntOrNull(KEY_BIRTH_MINUTE) ?: return null
        val place = AppDI.settings.getStringOrNull(KEY_BIRTH_PLACE)?.trim().orEmpty()
        if (place.isBlank()) return null

        return BirthDetails(
            dob = Dob(y, m, d),
            time = BirthTime(hh.coerceIn(0, 23), mm.coerceIn(0, 59)),
            place = place
        )
    }

    fun clear() {
        AppDI.settings.remove(KEY_BIRTH_YEAR)
        AppDI.settings.remove(KEY_BIRTH_MONTH)
        AppDI.settings.remove(KEY_BIRTH_DAY)
        AppDI.settings.remove(KEY_BIRTH_HOUR)
        AppDI.settings.remove(KEY_BIRTH_MINUTE)
        AppDI.settings.remove(KEY_BIRTH_PLACE)
    }
}
