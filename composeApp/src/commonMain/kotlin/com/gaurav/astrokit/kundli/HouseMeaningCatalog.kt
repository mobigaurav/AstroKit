package com.gaurav.astrokit.kundli

data class HouseMeaning(
    val title: String,
    val themes: String,
    val keywords: List<String>
)

object HouseMeaningCatalog {
    fun meaning(house: Int): HouseMeaning = when (house) {
        1 -> HouseMeaning("Self / Body", "Personality, vitality, appearance, approach to life.", listOf("Identity", "Health", "Confidence"))
        2 -> HouseMeaning("Wealth / Family", "Money, speech, values, family support, food habits.", listOf("Finances", "Speech", "Values"))
        3 -> HouseMeaning("Courage / Skills", "Effort, courage, communication, siblings, short travel.", listOf("Courage", "Skills", "Siblings"))
        4 -> HouseMeaning("Home / Mother", "Home, mother, comfort, property, inner peace.", listOf("Home", "Mother", "Property"))
        5 -> HouseMeaning("Creativity / Children", "Creativity, education, romance, children, intelligence.", listOf("Creativity", "Education", "Love"))
        6 -> HouseMeaning("Health / Service", "Diseases, debts, service, routines, competitors.", listOf("Health", "Work", "Discipline"))
        7 -> HouseMeaning("Partnership", "Marriage, business partnerships, contracts, public image.", listOf("Marriage", "Partnerships", "Contracts"))
        8 -> HouseMeaning("Transformation", "Sudden events, longevity, secrets, research, inheritance.", listOf("Change", "Mystery", "Longevity"))
        9 -> HouseMeaning("Fortune / Dharma", "Luck, higher learning, mentors, spirituality, long travel.", listOf("Luck", "Dharma", "Wisdom"))
        10 -> HouseMeaning("Career / Status", "Career, achievements, authority, reputation.", listOf("Career", "Fame", "Responsibility"))
        11 -> HouseMeaning("Gains / Network", "Gains, friendships, community, ambitions.", listOf("Income", "Friends", "Goals"))
        12 -> HouseMeaning("Loss / Moksha", "Expenses, isolation, foreign lands, sleep, liberation.", listOf("Expenses", "Foreign", "Spirituality"))
        else -> HouseMeaning("House", "Themes of life.", emptyList())
    }
}
