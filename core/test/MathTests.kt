package com.kurante.projectvoice_gdx.game

import com.kurante.projectvoice_gdx.util.extensions.mapRange
import org.junit.Test
import kotlin.test.assertNotEquals

class MathTests {
    @Test
    fun intMapRange() {
        val percent = 9049.mapRange(9049, 9049, 0f, 1f)
        assertNotEquals(percent, Float.NaN)
    }

    @Test
    fun linearNaN() {
        val percent = TransitionEase.LINEAR.fromTime(9049, 9049, 9049, 0f, 1f)
        assertNotEquals(percent, Float.NaN)
    }
}