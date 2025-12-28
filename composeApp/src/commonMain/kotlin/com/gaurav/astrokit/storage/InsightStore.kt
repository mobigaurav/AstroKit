package com.gaurav.astrokit.storage

import com.gaurav.astrokit.catalog.DailyInsight
import com.gaurav.astrokit.core.AppDI

private const val KEY_INSIGHT_DAYS = "insight_days" // pipe-separated list of dayKeys
private const val KEY_PREFIX = "insight_" // insight_<dayKey> -> serialized

object InsightStore {

    // dayKey like "2025-12-27"
    fun save(dayKey: String, insight: DailyInsight) {
        val serialized = serialize(insight)
        AppDI.settings.putString(KEY_PREFIX + dayKey, serialized)

        val existing = AppDI.settings.getStringOrNull(KEY_INSIGHT_DAYS)
            ?.split("|")
            ?.filter { it.isNotBlank() }
            ?.toMutableList()
            ?: mutableListOf()

        // move dayKey to front
        existing.remove(dayKey)
        existing.add(0, dayKey)

        // keep last 7
        val trimmed = existing.take(7)

        AppDI.settings.putString(KEY_INSIGHT_DAYS, trimmed.joinToString("|"))

        // cleanup older entries
        existing.drop(7).forEach { oldKey ->
            AppDI.settings.remove(KEY_PREFIX + oldKey)
        }
    }

    fun loadAll(): List<Pair<String, DailyInsight>> {
        val keys = AppDI.settings.getStringOrNull(KEY_INSIGHT_DAYS)
            ?.split("|")
            ?.filter { it.isNotBlank() }
            ?: emptyList()

        return keys.mapNotNull { k ->
            val raw = AppDI.settings.getStringOrNull(KEY_PREFIX + k) ?: return@mapNotNull null
            deserialize(raw)?.let { k to it }
        }
    }

    fun clear() {
        val keys = AppDI.settings.getStringOrNull(KEY_INSIGHT_DAYS)
            ?.split("|")
            ?.filter { it.isNotBlank() }
            ?: emptyList()

        keys.forEach { AppDI.settings.remove(KEY_PREFIX + it) }
        AppDI.settings.remove(KEY_INSIGHT_DAYS)
    }

    // Simple, safe serialization (no JSON dependency)
    private fun serialize(i: DailyInsight): String {
        // Replace newlines/pipes just in case
        fun esc(s: String) = s.replace("|", " ").replace("\n", " ")
        return listOf(esc(i.title), esc(i.message), esc(i.focus)).joinToString("|")
    }

    private fun deserialize(raw: String): DailyInsight? {
        val parts = raw.split("|")
        if (parts.size < 3) return null
        return DailyInsight(
            title = parts[0],
            message = parts[1],
            focus = parts[2]
        )
    }
}
