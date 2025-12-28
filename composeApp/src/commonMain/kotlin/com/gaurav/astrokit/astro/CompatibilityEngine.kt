package com.gaurav.astrokit.astro

data class CompatibilityResult(
    val score: Int,      // 0..100
    val label: String,   // "Excellent match"
    val summary: String  // 1-2 lines explanation
)

object CompatibilityEngine {

    fun between(a: ZodiacSign, b: ZodiacSign): CompatibilityResult {
        if (a == b) {
            return CompatibilityResult(
                score = 88,
                label = "High harmony",
                summary = "Same-sign match: strong understanding, but watch repeating patterns."
            )
        }

        val elementScore = elementScore(a.element, b.element)      // 0..60
        val modalityScore = modalityScore(a.modality, b.modality)  // 0..25
        val polarityBonus = polarityBonus(a.element, b.element)    // 10..15

        val score = (elementScore + modalityScore + polarityBonus).coerceIn(0, 100)

        val label = when {
            score >= 85 -> "Excellent match"
            score >= 70 -> "Great match"
            score >= 55 -> "Good potential"
            score >= 40 -> "Mixed vibes"
            else -> "Challenging"
        }

        val summary = buildSummary(a, b, score)
        return CompatibilityResult(score, label, summary)
    }

    private fun elementScore(a: Element, b: Element): Int = when (a) {
        Element.FIRE -> when (b) {
            Element.FIRE -> 52
            Element.AIR -> 58
            Element.EARTH -> 32
            Element.WATER -> 28
        }

        Element.AIR -> when (b) {
            Element.AIR -> 50
            Element.FIRE -> 58
            Element.EARTH -> 34
            Element.WATER -> 30
        }

        Element.EARTH -> when (b) {
            Element.EARTH -> 52
            Element.WATER -> 56
            Element.FIRE -> 32
            Element.AIR -> 34
        }

        Element.WATER -> when (b) {
            Element.WATER -> 52
            Element.EARTH -> 56
            Element.FIRE -> 28
            Element.AIR -> 30
        }
    }

    private fun modalityScore(a: Modality, b: Modality): Int = when (a) {
        Modality.CARDINAL -> when (b) {
            Modality.CARDINAL -> 18
            Modality.FIXED -> 20
            Modality.MUTABLE -> 22
        }

        Modality.FIXED -> when (b) {
            Modality.CARDINAL -> 20
            Modality.FIXED -> 15
            Modality.MUTABLE -> 21
        }

        Modality.MUTABLE -> when (b) {
            Modality.CARDINAL -> 22
            Modality.FIXED -> 21
            Modality.MUTABLE -> 16
        }
    }

    private fun polarityBonus(a: Element, b: Element): Int {
        val active = setOf(Element.FIRE, Element.AIR)
        val receptive = setOf(Element.EARTH, Element.WATER)
        return if ((a in active && b in receptive) || (a in receptive && b in active)) 15 else 10
    }

    private fun buildSummary(a: ZodiacSign, b: ZodiacSign, score: Int): String {
        val elementLine = when {
            a.element == b.element ->
                "Shared ${a.element.name.lowercase()} element: you naturally understand each other."
            (a.element == Element.FIRE && b.element == Element.AIR) || (a.element == Element.AIR && b.element == Element.FIRE) ->
                "Fire + Air: energetic and playful; momentum comes naturally."
            (a.element == Element.EARTH && b.element == Element.WATER) || (a.element == Element.WATER && b.element == Element.EARTH) ->
                "Earth + Water: stable and supportive; trust builds steadily."
            else ->
                "Different elements: attraction can be strong, but communication is key."
        }

        val modalityLine = when {
            a.modality == b.modality && a.modality == Modality.FIXED ->
                "Both Fixed: loyal and committed, but avoid power struggles."
            a.modality == b.modality ->
                "Both ${a.modality.name.lowercase()}: you move at a similar pace."
            else ->
                "Different modalities: one initiates, one stabilizes—great balance if respected."
        }

        val ending = when {
            score >= 70 -> "Lean into strengths and keep small rituals to stay connected."
            score >= 50 -> "With honesty and patience, this can grow beautifully."
            else -> "This match needs maturity—set clear boundaries and expectations."
        }

        return "$elementLine $modalityLine $ending"
    }
}
