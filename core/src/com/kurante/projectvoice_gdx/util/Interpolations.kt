package com.kurante.projectvoice_gdx.util

import com.badlogic.gdx.math.Interpolation
import kotlin.math.pow

object Interpolations {
    val outCubic = object : Interpolation() {
        override fun apply(t: Float): Float = 1f - (1 - t).pow(3)
    }
}
