package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.math.MathUtils.ceil


fun Float.toMillis(): Int = ceil(this * 1000f)
fun Int.toSeconds(): Float = this / 1000f