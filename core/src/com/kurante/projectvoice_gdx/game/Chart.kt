package com.kurante.projectvoice_gdx.game

import com.badlogic.gdx.graphics.Color


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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return (other as Track).id == id
    }

    override fun hashCode(): Int = id.hashCode()

    fun getMoveTransition(time: Int): Transition {
        var result = moveTransitions.first()

        for (transition in moveTransitions) {
            if (time >= transition.startTime)
                result = transition
            else
                return result
        }

        return moveTransitions.last()
    }

    fun getScaleTransition(time: Int): Transition {
        var result = scaleTransitions.first()

        for (transition in scaleTransitions) {
            if (time >= transition.startTime)
                result = transition
            else
                return result
        }

        return scaleTransitions.last()
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