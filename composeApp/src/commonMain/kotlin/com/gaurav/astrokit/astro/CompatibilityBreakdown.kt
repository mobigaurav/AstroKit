package com.gaurav.astrokit.astro

import com.gaurav.astrokit.astro.Element
import com.gaurav.astrokit.astro.Modality
import com.gaurav.astrokit.astro.ZodiacSign
import kotlin.math.abs

data class CompatibilityWhy(
    val elementScore: Int,   // 0..100
    val modalityScore: Int,  // 0..100
    val overallHint: String,
    val bullets: List<String>
)

object CompatibilityWhyEngine {

    fun explain(a: ZodiacSign, b: ZodiacSign): CompatibilityWhy {
        val eScore = elementScore(a.element, b.element)
        val mScore = modalityScore(a.modality, b.modality)

        val bullets = buildList {
            add(elementBullet(a.element, b.element, eScore))
            add(modalityBullet(a.modality, b.modality, mScore))
            add(archetypeBullet(a, b))
        }

        val hint = when {
            eScore >= 80 && mScore >= 70 -> "Strong natural flow + good day-to-day rhythm."
            eScore >= 80 -> "Great chemistry — align routines to avoid friction."
            mScore >= 75 -> "You work well as a team — keep communication clear."
            eScore <= 45 && mScore <= 45 -> "High contrast — can be growth-oriented if both are intentional."
            eScore <= 45 -> "Different emotional languages — clarity helps."
            else -> "Balanced match — depends on effort and timing."
        }

        return CompatibilityWhy(
            elementScore = eScore,
            modalityScore = mScore,
            overallHint = hint,
            bullets = bullets
        )
    }

    // --- Scoring rules (simple, explainable, deterministic) ---

    private fun elementScore(a: Element, b: Element): Int {
        if (a == b) return 75

        val pair = setOf(a, b)
        return when {
            // supportive pairs
            pair == setOf(Element.FIRE, Element.AIR) -> 90
            pair == setOf(Element.EARTH, Element.WATER) -> 90

            // mixed but workable
            pair == setOf(Element.FIRE, Element.EARTH) -> 60
            pair == setOf(Element.AIR, Element.WATER) -> 60

            // challenging
            pair == setOf(Element.FIRE, Element.WATER) -> 40
            pair == setOf(Element.AIR, Element.EARTH) -> 40

            else -> 55
        }
    }

    private fun modalityScore(a: Modality, b: Modality): Int {
        if (a == b) {
            return when (a) {
                Modality.CARDINAL -> 70 // both initiate; can clash
                Modality.FIXED -> 60    // loyalty + stubbornness
                Modality.MUTABLE -> 75  // adaptable
            }
        }

        // Different modalities can complement
        return 80
    }

    private fun elementBullet(a: Element, b: Element, score: Int): String {
        val tone = when {
            score >= 85 -> "Highly compatible"
            score >= 70 -> "Naturally compatible"
            score >= 55 -> "Mixed but workable"
            else -> "Challenging mix"
        }

        val explanation = when (setOf(a, b)) {
            setOf(Element.FIRE, Element.AIR) ->
                "Air fuels Fire — energy + ideas combine well."
            setOf(Element.EARTH, Element.WATER) ->
                "Water nourishes Earth — emotional support + stability."
            setOf(Element.FIRE, Element.EARTH) ->
                "Fire pushes forward; Earth prefers steady steps — align pace."
            setOf(Element.AIR, Element.WATER) ->
                "Air thinks; Water feels — translate emotions into words."
            setOf(Element.FIRE, Element.WATER) ->
                "Fire can overwhelm Water; Water can cool Fire — needs balance."
            setOf(Element.AIR, Element.EARTH) ->
                "Air changes quickly; Earth is practical — respect each other’s style."
            else ->
                "Different energies require a bit more intention."
        }

        return "Element harmony: $tone ($a + $b). $explanation"
    }

    private fun modalityBullet(a: Modality, b: Modality, score: Int): String {
        val tone = when {
            score >= 80 -> "Complementary"
            score >= 70 -> "Mostly aligned"
            score >= 55 -> "Some friction"
            else -> "Often clashes"
        }

        val explanation = if (a == b) {
            when (a) {
                Modality.CARDINAL -> "Both like to lead — decide how you make decisions."
                Modality.FIXED -> "Both are loyal but stubborn — soften rigidity."
                Modality.MUTABLE -> "Both adapt easily — keep long-term consistency."
            }
        } else {
            "Different styles can balance: one initiates, one stabilizes, one adapts."
        }

        return "Modality dynamic: $tone ($a + $b). $explanation"
    }

    private fun archetypeBullet(a: ZodiacSign, b: ZodiacSign): String {
        // Simple/pleasant rule-of-thumb: opposites share an axis; same element often shares vibe
        val opposite = oppositeOf(a)
        return if (opposite == b) {
            "Archetype: Opposite signs often bring strong attraction + growth through contrast."
        } else if (a.element == b.element) {
            "Archetype: Same-element signs share a natural vibe and “get” each other quickly."
        } else {
            "Archetype: Different archetypes — success comes from curiosity and communication."
        }
    }

    private fun oppositeOf(s: ZodiacSign): ZodiacSign = when (s) {
        ZodiacSign.ARIES -> ZodiacSign.LIBRA
        ZodiacSign.TAURUS -> ZodiacSign.SCORPIO
        ZodiacSign.GEMINI -> ZodiacSign.SAGITTARIUS
        ZodiacSign.CANCER -> ZodiacSign.CAPRICORN
        ZodiacSign.LEO -> ZodiacSign.AQUARIUS
        ZodiacSign.VIRGO -> ZodiacSign.PISCES
        ZodiacSign.LIBRA -> ZodiacSign.ARIES
        ZodiacSign.SCORPIO -> ZodiacSign.TAURUS
        ZodiacSign.SAGITTARIUS -> ZodiacSign.GEMINI
        ZodiacSign.CAPRICORN -> ZodiacSign.CANCER
        ZodiacSign.AQUARIUS -> ZodiacSign.LEO
        ZodiacSign.PISCES -> ZodiacSign.VIRGO
    }
}
