package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.math.MathUtils.ceil


fun Float.toMillis(): Int = ceil(this * 1000f)
fun Int.toSeconds(): Float = this / 1000f

fun Float.mapRange(fromMin: Float, fromMax: Float, toMin: Float, toMax: Float): Float {
    return (this - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin
}

fun Int.mapRange(fromMin: Float, fromMax: Float, toMin: Float, toMax: Float): Float {
    return (this - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin
}

fun Int.mapRange(fromMin: Int, fromMax: Int, toMin: Int, toMax: Int): Float {
    return (this - fromMin) * (toMax - toMin) / (fromMax - fromMin).toFloat() + toMin
}