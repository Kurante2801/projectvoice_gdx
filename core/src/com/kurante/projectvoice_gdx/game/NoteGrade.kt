package com.kurante.projectvoice_gdx.game

import kotlin.math.abs

enum class NoteGrade(val timing: Int, val weight: Double) {
    MISS(0, 0.0),
    GOOD(200, 0.5),
    GREAT(120, 0.75),
    PERFECT(40, 1.0),
    ;
    companion object {
        val missThreshold = GOOD.timing

        fun fromTime(difference: Int): NoteGrade? {
            // No grade, note is too far up in the screen to count as being interacted with
            if (difference > GOOD.timing) return null
            if (difference < -GOOD.timing) return MISS

            val time = abs(difference)
            return when {
                time <= PERFECT.timing -> PERFECT
                time <= GREAT.timing -> GREAT
                time <= GOOD.timing -> GOOD
                else -> null // In theory this can't be reached
            }
        }
    }
}
