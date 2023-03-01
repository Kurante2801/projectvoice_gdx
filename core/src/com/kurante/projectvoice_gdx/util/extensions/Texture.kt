package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils.*
import kotlin.math.abs

/**
 * Creates a region that has an aspect ratio of parentRatio but preserves childRatio.
 *
 * This function tries to imitate Unity's Aspect Ratio Fitter when set to Envelope Parent and an aspect ratio that differs from Texture's aspect ratio
 * @param parentRatio The Aspect Ratio of the container of this image (for example an Image actor or the screen itself)
 * @param childRatio The Aspect Ratio of the texture itself
 */
fun Texture.envelopeParent(parentRatio: Float, childRatio: Float = width.toFloat() / height): TextureRegion {
    return if (parentRatio >= childRatio) {
        val v = (1f / childRatio - 1f / parentRatio) * 0.5f
        TextureRegion(this, 0f, v, 1f, 1f - v)
    } else {
        val u = (childRatio - parentRatio) * 0.5f
        return TextureRegion(this, u, 0f, 1f - u, 1f)
    }
}

val Texture.aspectRatio: Float get() = width.toFloat() / height