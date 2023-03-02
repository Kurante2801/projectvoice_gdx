package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.graphics.Color

/**
 * Returns a new Color object with the same rgb values as the original.
 */
fun Color.withAlpha(alpha: Float): Color = Color(this.r, this.g, this.b, alpha)

/**
 * Sets the alpha value of this color and returns the same Color object
 */
fun Color.setAlpha(alpha: Float): Color = this.set(this.r, this.g, this.b, alpha)
fun Color.set(from: Color, alpha: Float): Color = this.set(from.r, from.g, from.b, alpha)