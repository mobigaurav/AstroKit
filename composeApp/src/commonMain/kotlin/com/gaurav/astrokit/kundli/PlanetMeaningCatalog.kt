package com.gaurav.astrokit.kundli

data class PlanetMeaning(
    val symbol: String,
    val short: String,
    val themes: List<String>
)

object PlanetMeaningCatalog {

    fun symbolOf(name: String): String {
        val n = name.trim().lowercase()
        return when {
            n.contains("sun") -> "☉"
            n.contains("moon") -> "☾"
            n.contains("mars") -> "♂"
            n.contains("mercury") -> "☿"
            n.contains("venus") -> "♀"
            n.contains("jupiter") -> "♃"
            n.contains("saturn") -> "♄"
            n.contains("rahu") || n.contains("north") -> "☊"
            n.contains("ketu") || n.contains("south") -> "☋"
            // Only if your model includes these:
            n.contains("uranus") -> "♅"
            n.contains("neptune") -> "♆"
            n.contains("pluto") -> "♇"
            else -> "✦"
        }
    }

    fun meaning(name: String): PlanetMeaning {
        val n = name.trim().lowercase()
        return when {
            n.contains("sun") -> PlanetMeaning("☉", "Ego, vitality, leadership.", listOf("Authority", "Confidence", "Purpose"))
            n.contains("moon") -> PlanetMeaning("☾", "Mind, emotions, comfort.", listOf("Emotions", "Mother", "Stability"))
            n.contains("mars") -> PlanetMeaning("♂", "Energy, action, courage.", listOf("Drive", "Courage", "Competition"))
            n.contains("mercury") -> PlanetMeaning("☿", "Intellect, speech, trade.", listOf("Communication", "Logic", "Business"))
            n.contains("venus") -> PlanetMeaning("♀", "Love, beauty, pleasure.", listOf("Relationships", "Art", "Luxury"))
            n.contains("jupiter") -> PlanetMeaning("♃", "Wisdom, expansion, luck.", listOf("Growth", "Knowledge", "Blessings"))
            n.contains("saturn") -> PlanetMeaning("♄", "Discipline, karma, patience.", listOf("Hard work", "Delay", "Structure"))
            n.contains("rahu") || n.contains("north") ->
                PlanetMeaning("☊", "Desire, ambition, obsession.", listOf("Material growth", "Fame", "Unconventional"))
            n.contains("ketu") || n.contains("south") ->
                PlanetMeaning("☋", "Detachment, spirituality, past karma.", listOf("Letting go", "Mysticism", "Liberation"))
            n.contains("uranus") -> PlanetMeaning("♅", "Sudden change, innovation.", listOf("Breakthrough", "Freedom", "Shock"))
            n.contains("neptune") -> PlanetMeaning("♆", "Dreams, intuition, illusion.", listOf("Imagination", "Spirituality", "Confusion"))
            n.contains("pluto") -> PlanetMeaning("♇", "Power, deep transformation.", listOf("Rebirth", "Intensity", "Control"))
            else -> PlanetMeaning(symbolOf(name), "General influence.", listOf("Influence"))
        }
    }
}
