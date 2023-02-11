package com.kurante.projectvoice_gdx.game

import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.sin
import com.kurante.projectvoice_gdx.ProjectVoice.Companion.PI

// https://github.com/AndrewFM/VoezEditor/blob/master/Assets/Scripts/ProjectData.cs#L548
enum class TransitionEase(
    val easeFunction: (Float, Float, Float) -> Float
) {
    NONE({ _, _, end -> end }),
    LINEAR({ perc, start, end ->
        (end - start) * perc + start
    }),
    EXP_IN({ perc, start, end ->
        val x = 2f.pow(10f * (perc - 1f))
        (end - start) * x + start
    }),
    EXP_OUT({ perc, start, end ->
        val x = -(2f.pow(10f * -perc)) + 1f
        (end - start) * x + start
    }),
    EXP_INOUT({ perc, start, end ->
        val x = if (perc < 0.5f)
            0.5f * 2f.pow(10f * (2f * perc - 1f))
        else
            -(2f.pow(-10f * (2f * perc - 1f))) + 1f
        (end - start) * x + start
    }),
    EXP_OUTIN({ perc, start, end ->
        val x = when {
            (perc <= 0f) -> 0f
            (perc >= 1f) -> 1f
            (perc < 0.5f) -> ln(2048f * perc) / (20f * ln(2f))
            else -> ln(-512f / (perc - 1f)) / (20f * ln(2f))
        }
        (end - start) * x + start
    }),
    QUAD_IN({ perc, start, end ->
        val x = perc * perc
        (end - start) * x + start
    }),
    QUAD_OUT({ perc, start, end ->
        val x = -((1f - perc) * (1f - perc)) + 1f
        (end - start) * x + start
    }),
    QUAD_INOUT({ perc, start, end ->
        val x = if (perc < 0.5f) perc * perc * 2f else -1f + (4f - 2f * perc) * perc
        (end - start) * x + start
    }),
    QUAD_OUTIN({ perc, start, end ->
        val x = if (perc < 0.5f) sqrt(perc / 2f) else 1f - (sqrt(1f - perc) / sqrt(2f))
        (end - start) * x + start
    }),
    CIRC_IN({ perc, start, end ->
        val x = 1f - sqrt(1f - perc * perc)
        (end - start) * x + start
    }),
    CIRC_OUT({ perc, start, end ->
        val x = -(1f - sqrt(1f - (1f - perc) * (1f - perc))) + 1f
        (end - start) * x + start
    }),
    CIRC_INOUT({ perc, start, end ->
        val x = if (perc < 0.5f)
            -0.5f * (sqrt(1f - 4f * perc * perc) - 1f)
        else
            0.5f * (sqrt(1f - (2f * perc - 2f) * (2f * perc - 2f)) + 1f)
        (end - start) * x + start
    }),
    CIRC_OUTIN({ perc, start, end ->
        val x = if (perc < 0.5f) sqrt(perc - perc * perc) else 1f - sqrt(perc - perc * perc)
        (end - start) * x + start
    }),
    BACK_IN({ perc, start, end ->
        val x = perc * perc * (2.70158f * perc - 1.70158f)
        (end - start) * x + start
    }),
    BACK_OUT({ perc, start, end ->
        val x = -((1f - perc) * (1f - perc) * (2.70158f * (1f - perc) - 1.70158f)) + 1f
        (end - start) * x + start
    }),
    BACK_INOUT({ perc, start, end ->
        val x = if (perc < 0.5f)
            2f * perc * (7.189819f * perc - 2.5949095f)
        else
            0.5f * ((2f * perc - 2f) * (2f * perc - 2f) * (3.5949095f * (2f * perc - 2f) + 2.5949095f) + 2f)
        (end - start) * x + start
    }),
    BACK_OUTIN({ perc, start, end ->
        val x = if (perc >= 0.5f)
            2f * (perc - 0.5f) * (perc - 0.5f) * (7.189819f * (perc - 0.5f) - 2.5949095f) + 0.5f
        else
            0.5f * ((2f * (perc + 0.5f) - 2f) * (2f * (perc + 0.5f) - 2f) * (3.5949095f * (2f * (perc + 0.5f) - 2f) + 2.5949095f) + 2f) - 0.5f
        (end - start) * x + start
    }),
    ELASTIC_IN({ perc, start, end ->
        val x = -(2f.pow(10f * (perc - 1f)) * sin((perc - 1.1f) * 2f * PI / 0.4f))
        (end - start) * x + start
    }),
    ELASTIC_OUT({ perc, start, end ->
        val x = 2f.pow(-10f * perc) * sin((perc - 0.1f) * 2f * PI / 0.4f) + 1f
        (end - start) * x + start
    }),
    ELASTIC_INOUT({ perc, start, end ->
        val x = if (perc < 0.5f)
            -0.5f * 2f.pow(10f * (2f * perc - 1)) * sin(((2f * perc - 1) - 0.1f) * 2f * PI / 0.4f)
        else
            0.5f * 2f.pow(-10f * (2f * perc - 1)) * sin(((2f * perc - 1) - 0.1f) * 2f * PI / 0.4f) + 1f
        (end - start) * x + start
    }),
    ELASTIC_OUTIN({ perc, start, end ->
        val x = if (perc >= 0.5f)
            (-0.5f * 2f.pow(10f * (2f * (perc - 0.5f) - 1f)) * sin(((2f * (perc - 0.5f) - 1f) - 0.1f) * 2f * PI / 0.4f)) + 0.5f
        else
            (0.5f * 2f.pow(-10f * (2f * (perc + 0.5f) - 1f)) * sin(((2f * (perc + 0.5f) - 1f) - 0.1f) * 2f * PI / 0.4f) + 1f) - 0.5f
        (end - start) * x + start
    }),
    EXIT({ perc, start, end ->
        end
    }),
    EXIT_MOVE({ perc, start, end ->
        val newEnd = if (end > start) 10f else -10f
        val x = perc * perc * (2.70158f * (perc * 2f) - 1.20158f)
        (newEnd - start) * x + start
    }),
    EXIT_SCALE({ perc, start, end ->
        val newEnd = if (end > start) 20f else -20f
        val x = perc * perc * (2.70158f * (perc * 4f) - 1.20158f)
        (newEnd - start) * x + start
    }),
    EXIT_COLOR({ perc, start, end ->
        val x = perc * perc * (2.70158f * (perc * 6f) - 1.00158f)
        (end - start) * x + start
    }),
}
