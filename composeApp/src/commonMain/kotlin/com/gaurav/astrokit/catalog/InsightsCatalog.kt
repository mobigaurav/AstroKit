package com.gaurav.astrokit.catalog

import com.gaurav.astrokit.astro.ZodiacSign

data class InsightBlock(
    val title: String,
    val bullets: List<String>
)

object InsightsCatalog {

    fun zodiacTraits(sign: ZodiacSign): InsightBlock {
        return when (sign) {
            ZodiacSign.ARIES -> InsightBlock(
                "Aries traits",
                listOf("Bold, action-first energy", "Natural leader", "Watch-outs: impatience, impulsive decisions")
            )
            ZodiacSign.TAURUS -> InsightBlock(
                "Taurus traits",
                listOf("Grounded, consistent, loyal", "Strong taste and values", "Watch-outs: stubbornness, resisting change")
            )
            ZodiacSign.GEMINI -> InsightBlock(
                "Gemini traits",
                listOf("Curious, witty, adaptable", "Great communicator", "Watch-outs: overthinking, scattered focus")
            )
            ZodiacSign.CANCER -> InsightBlock(
                "Cancer traits",
                listOf("Protective, intuitive, nurturing", "Deep emotional memory", "Watch-outs: mood swings, taking things personally")
            )
            ZodiacSign.LEO -> InsightBlock(
                "Leo traits",
                listOf("Confident, warm, expressive", "Loves recognition and loyalty", "Watch-outs: pride, needing validation")
            )
            ZodiacSign.VIRGO -> InsightBlock(
                "Virgo traits",
                listOf("Detail-oriented, helpful, practical", "Strong sense of improvement", "Watch-outs: perfectionism, self-criticism")
            )
            ZodiacSign.LIBRA -> InsightBlock(
                "Libra traits",
                listOf("Balanced, charming, fair-minded", "Great at partnerships", "Watch-outs: indecision, people-pleasing")
            )
            ZodiacSign.SCORPIO -> InsightBlock(
                "Scorpio traits",
                listOf("Intense, focused, resilient", "Loyal to the core", "Watch-outs: secrecy, holding grudges")
            )
            ZodiacSign.SAGITTARIUS -> InsightBlock(
                "Sagittarius traits",
                listOf("Adventurous, optimistic, honest", "Big-picture thinker", "Watch-outs: bluntness, restlessness")
            )
            ZodiacSign.CAPRICORN -> InsightBlock(
                "Capricorn traits",
                listOf("Disciplined, ambitious, reliable", "Long-term builder", "Watch-outs: rigidity, work-life imbalance")
            )
            ZodiacSign.AQUARIUS -> InsightBlock(
                "Aquarius traits",
                listOf("Innovative, independent, future-minded", "Values freedom and ideas", "Watch-outs: emotional distance, stubborn opinions")
            )
            ZodiacSign.PISCES -> InsightBlock(
                "Pisces traits",
                listOf("Compassionate, imaginative, intuitive", "Deep empathy", "Watch-outs: escapism, absorbing others’ emotions")
            )
        }
    }

    fun lifePathInsight(lifePath: Int): InsightBlock {
        return when (lifePath) {
            1 -> InsightBlock("Life Path 1", listOf("Leader energy", "Independence & initiative", "Watch-outs: ego, impatience"))
            2 -> InsightBlock("Life Path 2", listOf("Peacemaker", "Partnership & diplomacy", "Watch-outs: sensitivity, over-giving"))
            3 -> InsightBlock("Life Path 3", listOf("Creator", "Expression & joy", "Watch-outs: inconsistency, seeking approval"))
            4 -> InsightBlock("Life Path 4", listOf("Builder", "Structure & discipline", "Watch-outs: rigidity, overwork"))
            5 -> InsightBlock("Life Path 5", listOf("Explorer", "Freedom & change", "Watch-outs: impulsiveness, instability"))
            6 -> InsightBlock("Life Path 6", listOf("Caretaker", "Responsibility & love", "Watch-outs: control, over-sacrifice"))
            7 -> InsightBlock("Life Path 7", listOf("Seeker", "Wisdom & introspection", "Watch-outs: isolation, skepticism"))
            8 -> InsightBlock("Life Path 8", listOf("Achiever", "Power & abundance", "Watch-outs: material fixation, stress"))
            9 -> InsightBlock("Life Path 9", listOf("Humanitarian", "Compassion & completion", "Watch-outs: emotional burnout, letting go"))
            11 -> InsightBlock("Master 11", listOf("Intuition amplifier", "Vision & inspiration", "Watch-outs: anxiety, nervous energy"))
            22 -> InsightBlock("Master 22", listOf("Master builder", "Big vision made real", "Watch-outs: pressure, fear of failure"))
            33 -> InsightBlock("Master 33", listOf("Teacher healer", "Service & upliftment", "Watch-outs: boundaries, emotional overload"))
            else -> InsightBlock("Life Path", listOf("Unique journey", "Focus on alignment and growth"))
        }
    }

    fun personalYearInsight(py: Int): InsightBlock {
        return when (py) {
            1 -> InsightBlock("Personal Year 1", listOf("Fresh starts", "New goals and identity", "Act boldly, start small"))
            2 -> InsightBlock("Personal Year 2", listOf("Partnerships", "Patience and planning", "Nurture relationships"))
            3 -> InsightBlock("Personal Year 3", listOf("Creativity", "Visibility and joy", "Share your voice"))
            4 -> InsightBlock("Personal Year 4", listOf("Foundation", "Work and systems", "Consistency wins"))
            5 -> InsightBlock("Personal Year 5", listOf("Change", "Travel / upgrades", "Stay flexible"))
            6 -> InsightBlock("Personal Year 6", listOf("Home & love", "Family responsibilities", "Balance giving"))
            7 -> InsightBlock("Personal Year 7", listOf("Inner work", "Learning and reflection", "Protect your peace"))
            8 -> InsightBlock("Personal Year 8", listOf("Money & career", "Leadership and results", "Be strategic"))
            9 -> InsightBlock("Personal Year 9", listOf("Completion", "Letting go", "Close chapters gracefully"))
            else -> InsightBlock("Personal Year", listOf("Focus on mindful progress"))
        }
    }
}
