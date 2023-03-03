package com.kurante.projectvoice_gdx.game

import com.kurante.projectvoice_gdx.util.extensions.toMillis

// To test
class SoundlessConductor(
    override val duration: Int,
    private val speed: Float = 1f,
    pos: Int = 0,
) : Conductor() {
    init {
        time = pos
    }

    override fun act(delta: Float) {
        if (paused || time >= maxTime) return
        time += (speed * delta).toMillis()
    }

    override fun restart() {
        time = 0
    }

    override fun dispose() { }
}