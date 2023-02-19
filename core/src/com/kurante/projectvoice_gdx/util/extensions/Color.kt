package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.graphics.Color

fun Color.withAlpha(alpha: Float) = this.set(this.r, this.g, this.b, alpha)