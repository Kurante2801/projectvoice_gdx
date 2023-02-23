package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.graphics.Color

fun Color.withAlpha(alpha: Float): Color = this.set(this.r, this.g, this.b, alpha)
fun Color.set(from: Color, alpha: Float): Color = this.set(from.r, from.g, from.b, alpha)