package com.gaurav.astrokit.astro

data class BestMatch(
    val sign: ZodiacSign,
    val result: CompatibilityResult
)

object BestMatches {
    fun top3(self: ZodiacSign): List<BestMatch> {
        val all = ZodiacSign.entries
            .filter { it != self }
            .map { other -> BestMatch(other, CompatibilityEngine.between(self, other)) }
            .sortedByDescending { it.result.score }

        return all.take(3)
    }
}
