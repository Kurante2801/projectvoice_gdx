package com.kurante.projectvoice_gdx.game

import com.badlogic.gdx.graphics.Color
import com.kurante.projectvoice_gdx.util.extensions.mapRange
import kotlin.math.abs


data class Chart(
    val startTime: Int = 0,
    val musicOffset: Int = 0,
    val tracks: Array<Track> = arrayOf()
)

data class Track(
    val id: Int,
    val spawnTime: Int,
    val spawnDuration: Int,
    val despawnTime: Int,
    val despawnDuration: Int,

    val moveTransitions: Array<Transition>,
    val scaleTransitions: Array<Transition>,
    val colorTransitions: Array<ColorTransition>,
) {
    companion object {
        // 120px at a window width of 1280px
        const val MARGIN_MIN = 0.09375f
        const val MARGIN_MAX = 0.90625f
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return (other as Track).id == id
    }

    override fun hashCode(): Int = id.hashCode()

    fun getTransition(time: Int, transitions: Array<Transition>): Transition {
        var result = transitions.first()

        for (transition in transitions) {
            if (time >= transition.startTime)
                result = transition
            else
                return result
        }

        return transitions.last()
    }

    fun getColorTransition(time: Int): ColorTransition {
        var result = colorTransitions.first()

        for (transition in colorTransitions) {
            if (time >= transition.startTime)
                result = transition
            else
                return result
        }

        return colorTransitions.last()
    }

    fun getPosition(time: Int): Float {
        val transition = getTransition(time, moveTransitions)
        val x = transition.easing.fromTime(time, transition.startTime, transition.endTime, transition.startValue, transition.endValue)
        // Editor has a margin of 120px on both left and right (with a window width of 1280px)
        // This means that if the track's position is 0, its center will be at 120px (at a window width of 1280px)
        return x.mapRange(0f, 1f, MARGIN_MIN, MARGIN_MAX)
    }

    fun getWidth(time: Int, trackWidth: Float, trackBorders: Float): Float {
        val transition = getTransition(time, scaleTransitions)
        val w = transition.easing.fromTime(time, transition.startTime, transition.endTime, transition.startValue, transition.endValue)
        // Track's border glows make the track visually smaller
        return abs(w * trackWidth - trackBorders)
    }

    fun getColor(time: Int): Color {
        val transition = getColorTransition(time)
        val percent = transition.easing.fromTime(time, transition.startTime, transition.endTime, 0f, 1f)
        return transition.startValue.lerp(transition.endValue, percent)
    }
}

data class Transition(
    val easing: TransitionEase,
    val startTime: Int,
    val endTime: Int,
    val startValue: Float,
    val endValue: Float,
)

data class ColorTransition(
    val easing: TransitionEase,
    val startTime: Int,
    val endTime: Int,
    val startValue: Color,
    val endValue: Color,
)