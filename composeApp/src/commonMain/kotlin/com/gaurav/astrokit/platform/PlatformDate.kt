package com.gaurav.astrokit.platform

/**
 * Returns current year in local time zone.
 */
expect fun currentYear(): Int

/**
 * Returns current day-of-week short label like: "Mon", "Tue", "Wed"
 */
expect fun currentDayOfWeekShort(): String

/**
 * Stable local date key like "2025-12-27" (used for daily seed + history keys)
 */
expect fun todayKey(): String