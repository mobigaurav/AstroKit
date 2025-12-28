package com.gaurav.astrokit.platform

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.*
actual fun currentYear(): Int {
    val calendar = NSCalendar.currentCalendar
    val components = calendar.components(NSCalendarUnitYear, fromDate = NSDate())
    return components.year.toInt()
}

actual fun currentDayOfWeekShort(): String {
    // iOS: Sunday=1 ... Saturday=7
    val calendar = NSCalendar.currentCalendar
    val weekday = calendar.component(
        platform.Foundation.NSCalendarUnitWeekday,
        fromDate = NSDate()
    )

    return when (weekday.toInt()) {
        1 -> "Sun"
        2 -> "Mon"
        3 -> "Tue"
        4 -> "Wed"
        5 -> "Thu"
        6 -> "Fri"
        7 -> "Sat"
        else -> "Today"
    }
}

actual fun todayKey(): String {
    val calendar = NSCalendar.currentCalendar
    val comps = calendar.components(
        NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay,
        fromDate = NSDate()
    )
    val y = comps.year.toInt()
    val m = comps.month.toInt()
    val d = comps.day.toInt()
    fun pad2(x: Int) = x.toString().padStart(2, '0')
    return "${y.toString().padStart(4, '0')}-${pad2(m)}-${pad2(d)}"
}