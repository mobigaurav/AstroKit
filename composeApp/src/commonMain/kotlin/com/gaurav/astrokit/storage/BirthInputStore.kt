package com.gaurav.astrokit.storage

import com.gaurav.astrokit.core.AppDI
import com.gaurav.astrokit.kundli.BirthInput
import com.gaurav.astrokit.kundli.GeoPlace
import com.gaurav.astrokit.kundli.SimpleDate
import com.gaurav.astrokit.kundli.SimpleTime

private const val KEY_Y = "birth_y"
private const val KEY_M = "birth_m"
private const val KEY_D = "birth_d"
private const val KEY_H = "birth_h"
private const val KEY_MIN = "birth_min"
private const val KEY_TZ = "birth_tz_min"
private const val KEY_PLACE_NAME = "birth_place_name"
private const val KEY_PLACE_LAT = "birth_place_lat"
private const val KEY_PLACE_LON = "birth_place_lon"

object BirthInputStore {

    fun save(input: BirthInput) {
        AppDI.settings.putInt(KEY_Y, input.date.year)
        AppDI.settings.putInt(KEY_M, input.date.month)
        AppDI.settings.putInt(KEY_D, input.date.day)

        AppDI.settings.putInt(KEY_H, input.time.hour)
        AppDI.settings.putInt(KEY_MIN, input.time.minute)

        AppDI.settings.putInt(KEY_TZ, input.timezoneOffsetMinutes)

        AppDI.settings.putString(KEY_PLACE_NAME, input.place.name)
        AppDI.settings.putDouble(KEY_PLACE_LAT, input.place.lat)
        AppDI.settings.putDouble(KEY_PLACE_LON, input.place.lon)
    }

    fun loadOrNull(): BirthInput? {
        val y = AppDI.settings.getIntOrNull(KEY_Y) ?: return null
        val m = AppDI.settings.getIntOrNull(KEY_M) ?: return null
        val d = AppDI.settings.getIntOrNull(KEY_D) ?: return null
        val h = AppDI.settings.getIntOrNull(KEY_H) ?: return null
        val min = AppDI.settings.getIntOrNull(KEY_MIN) ?: return null
        val tz = AppDI.settings.getIntOrNull(KEY_TZ) ?: return null

        val name = AppDI.settings.getStringOrNull(KEY_PLACE_NAME) ?: return null
        val lat = AppDI.settings.getDoubleOrNull(KEY_PLACE_LAT) ?: return null
        val lon = AppDI.settings.getDoubleOrNull(KEY_PLACE_LON) ?: return null

        return BirthInput(
            date = SimpleDate(y, m, d),
            time = SimpleTime(h, min),
            timezoneOffsetMinutes = tz,
            place = GeoPlace(name, lat, lon)
        )
    }

    fun clear() {
        AppDI.settings.remove(KEY_Y)
        AppDI.settings.remove(KEY_M)
        AppDI.settings.remove(KEY_D)
        AppDI.settings.remove(KEY_H)
        AppDI.settings.remove(KEY_MIN)
        AppDI.settings.remove(KEY_TZ)
        AppDI.settings.remove(KEY_PLACE_NAME)
        AppDI.settings.remove(KEY_PLACE_LAT)
        AppDI.settings.remove(KEY_PLACE_LON)
    }
}
