package com.kurante.projectvoice_gdx.game

import com.badlogic.gdx.utils.Disposable

abstract class Conductor : Disposable {
    open var paused: Boolean = false

    // MILLISECONDS
    var time: Int = 0
    var minTime: Int = 10
    var maxTime: Int = 10
    open val duration: Int = 10

    abstract fun act(delta: Float)
}