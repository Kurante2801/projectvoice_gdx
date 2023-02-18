package com.kurante.projectvoice_gdx.game

import com.badlogic.gdx.Gdx
import com.kurante.projectvoice_gdx.util.extensions.mapRange
import kotlin.math.abs

class InputHandler(private val logic: GameplayLogic) {
    data class Finger(
        val id: Int, var x: Float,
        var touched: Boolean, var swiped: Boolean,
        var startX: Float, var swipeDelta: Int,
    )

    var width: Float = 0f
    val fingers: Array<Finger>
    init {
        val list = mutableListOf<Finger>()
        for (i in 0 until Gdx.input.maxPointers)
            list.add(Finger(i, 0f, false, false, 0f, 0))
        fingers = list.toTypedArray()
    }

    fun poll(width: Float) {
        this.width = width
        val swipeThreshold = width / 100f

        for (finger in fingers) {
            if (Gdx.input.isTouched(finger.id)) {
                // Convert screen space to stage space (width param)
                finger.x = Gdx.input.getX(finger.id).mapRange(0, Gdx.graphics.width, 0f, width)
                // Just touched
                if (!finger.touched) {
                    finger.touched = true
                    finger.startX = finger.x
                    inputDown(finger)
                }
                // Just swiped
                if (!finger.swiped && abs(finger.x - finger.startX) >= swipeThreshold) {
                    finger.swiped = true
                    finger.swipeDelta = if(finger.x > finger.startX) 1 else -1
                    inputSwiped(finger)
                }
                inputUpdate(finger)
            } else if (finger.touched) {
                finger.touched = false
                finger.swiped = false
            }
        }
    }

    fun inputDown(finger: Finger) {

    }

    fun inputUpdate(finger: Finger) {
        for ((track, info) in logic.tracks) {
            if (!info.shouldDraw || info.animating) continue

            val half = info.inputWidth * 0.5f
            if (finger.x >= info.center - half && finger.x <= info.center + half) {
                info.activeTime = logic.time
            }
        }
    }

    fun inputSwiped(finger: Finger) {

    }
}