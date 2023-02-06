package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils.floor

/**
 * Imitates Unity's Aspect Ratio Fitter when set to Envelope Parent.
 * This function name may not be the best...
 */
fun Texture.envelopeRatio(aspectRatio: Float): TextureRegion {
    return if((width.toFloat() / height) <= aspectRatio) {
        val h = floor(width / aspectRatio)
        TextureRegion(this, 0, floor((height - h) * 0.5f), width, h)
    } else {
        val w = floor(height * aspectRatio)
        TextureRegion(this, floor((width - w) * 0.5f), 0, w, height)
    }
}