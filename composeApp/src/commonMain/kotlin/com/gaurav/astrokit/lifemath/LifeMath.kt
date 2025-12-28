package com.gaurav.astrokit.lifemath

object Numerology {

    /**
     * Life Path Number:
     * Reduce YYYYMMDD digits to 1..9, keep 11, 22, 33 as master numbers.
     */
    fun lifePath(year: Int, month: Int, day: Int): Int {
        val digits = (year.toString() + month.toString().padStart(2, '0') + day.toString().padStart(2, '0'))
            .map { it.digitToInt() }
            .sum()
        return reduceWithMasters(digits)
    }

    /**
     * Personal Year:
     * Reduce (month + day + currentYear) with masters.
     */
    fun personalYear(month: Int, day: Int, currentYear: Int): Int {
        val sum = reduceWithMasters(month) + reduceWithMasters(day) + reduceWithMasters(currentYear)
        return reduceWithMasters(sum)
    }

    private fun reduceWithMasters(n: Int): Int {
        var x = n
        while (x > 9 && x != 11 && x != 22 && x != 33) {
            x = x.toString().map { it.digitToInt() }.sum()
        }
        return x
    }
}
