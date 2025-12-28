package com.gaurav.astrokit.catalog

import com.gaurav.astrokit.AstroProfile
import com.gaurav.astrokit.platform.currentDayOfWeekShort
import com.gaurav.astrokit.platform.todayKey

data class DailyInsight(
    val title: String,
    val message: String,
    val focus: String
)

object DailyInsightGenerator {

    fun generate(profile: AstroProfile): DailyInsight {
        val key = todayKey()
        val dow = currentDayOfWeekShort()
        val seed = stableSeed("$key|${profile.zodiac.name}|${profile.lifePath}|${profile.personalYear}")

        val focusPool = focusPool(profile.personalYear)
        val messagePool = messagePool(profile)

        val focus = focusPool[seed % focusPool.size]
        val message = messagePool[(seed / 7) % messagePool.size]

        return DailyInsight(
            title = "Today • $dow • $key",
            message = message,
            focus = focus
        )
    }

    fun shareText(profile: AstroProfile, insight: DailyInsight): String {
        return buildString {
            appendLine("AstroKit Insight")
            appendLine(insight.title)
            appendLine()
            appendLine("Zodiac: ${profile.zodiac.displayName} (${profile.zodiac.element})")
            appendLine("Life Path: ${profile.lifePath} • Personal Year: ${profile.personalYear}")
            appendLine()
            appendLine(insight.message)
            appendLine()
            appendLine("Focus: ${insight.focus}")
        }.trim()
    }

    private fun focusPool(personalYear: Int): List<String> = when (personalYear) {
        1 -> listOf("Start the thing you keep delaying.", "Make a bold first move.", "Begin small—begin today.")
        2 -> listOf("Choose patience over pressure.", "Strengthen one key relationship.", "Listen first, respond second.")
        3 -> listOf("Create, share, and be seen.", "Make it fun again.", "Your voice is your advantage.")
        4 -> listOf("Build one system that saves time.", "Consistency beats intensity.", "Finish what you started.")
        5 -> listOf("Say yes to a fresh experience.", "Change one habit.", "Stay flexible—don’t overcommit.")
        6 -> listOf("Take care of home and health.", "Support someone (with boundaries).", "Stability is your power.")
        7 -> listOf("Reduce noise and reflect.", "Learn something deeper.", "Protect your energy.")
        8 -> listOf("Make a strategic money move.", "Lead with clarity.", "Choose results over drama.")
        9 -> listOf("Close an unfinished loop.", "Let go of what drains you.", "Completion creates freedom.")
        else -> listOf("Keep it simple and steady.", "Do less, better.", "Small wins compound.")
    }

    private fun messagePool(profile: AstroProfile): List<String> {
        val elementLine = when (profile.zodiac.element.name) {
            "FIRE" -> "Lead with courage, but avoid rushing the details."
            "EARTH" -> "Ground your choices—slow progress is still progress."
            "AIR" -> "Communicate clearly; the right words open doors."
            "WATER" -> "Trust intuition—protect your emotional bandwidth."
            else -> "Stay balanced and focused."
        }

        val lpLine = when (profile.lifePath) {
            1 -> "Take initiative—your direction sets the tone."
            2 -> "Harmony matters, but don’t abandon boundaries."
            3 -> "Creativity is your shortcut today."
            4 -> "Discipline becomes your superpower."
            5 -> "Freedom is good—avoid impulsive decisions."
            6 -> "Care deeply, but don’t overcarry."
            7 -> "Seek truth; limit distractions."
            8 -> "Think long-term; act strategically."
            9 -> "Release what drains you; keep what grows you."
            11 -> "Intuition is loud—listen carefully."
            22 -> "Build something real from the vision."
            33 -> "Lead with compassion; protect your boundaries."
            else -> "Stay aligned with your values."
        }

        return listOf(
            "$elementLine $lpLine",
            "$lpLine $elementLine",
            "Today is about precision + pace. $elementLine",
            "One clear decision beats ten half-decisions. $lpLine"
        )
    }

    private fun stableSeed(input: String): Int {
        // Deterministic hash, no randomness, no platform dependencies
        var h = 0
        input.forEach { ch ->
            h = (h * 31 + ch.code) and 0x7fffffff
        }
        return h
    }
}
