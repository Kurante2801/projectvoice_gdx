package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.math.MathUtils.ceil

fun Float.toMillis(): Int = ceil(this * 1000f)
fun Int.toSeconds(): Float = this / 1000f

/**
 * Returns fallback if this is null
 */
fun Float.mapRange(fromMin: Float, fromMax: Float, toMin: Float, toMax: Float): Float {
    val range = (toMax - toMin) / (fromMax - fromMin)
    return if (range.isNaN() || range.isInfinite()) toMax else (this - fromMin) * range + toMin
}

fun Int.mapRange(fromMin: Int, fromMax: Int, toMin: Float, toMax: Float): Float {
    val range = (toMax - toMin) / (fromMax - fromMin)
    return if (range.isNaN() || range.isInfinite()) toMax else (this - fromMin) * range + toMin
}

fun Int.mapRange(fromMin: Int, fromMax: Int, toMin: Double, toMax: Double): Double {
    val range = (toMax - toMin) / (fromMax - fromMin)
    return if (range.isNaN() || range.isInfinite()) toMax else (this - fromMin) * range + toMin
}

// Assume fromMin = 0 and fromMax = 1f
fun Float.mapRange(toMin: Float, toMax: Float, clamped: Boolean = true): Float {
    return if (clamped)
        this.coerceIn(0f, 1f) * (toMax - toMin) + toMin
    else
        this * (toMax - toMin) + toMin
}

fun Float.lerp(b: Float, t: Float): Float = this + (b - this) * t.coerceIn(0f, 1f)