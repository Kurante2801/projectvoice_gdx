package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.math.MathUtils.ceil
import com.badlogic.gdx.math.MathUtils.floor

// Legacy background images have an aspect ratio of 1:1
// yet on legacy they're displayed at an aspect ratio of 16:9 using Unity's AspectRatioFitter (Envelope Parent)
// This function tries to imitate that

/**
 * Imitates Unity's Aspect Ratio Fitter when set to Envelope Parent.
 * Does NOT dispose original Pixmap.
 *
 * @param width Resulting width
 * @param height Resulting height
 * @param aspectRatio The aspect ratio the original pixmap will be resized to
*/
fun Pixmap.crop(
    width: Int,
    height: Int,
    aspectRatio: Float,
): Pixmap {
    val h = height * aspectRatio
    val pixmap = Pixmap(width, height, this.format)

    if(width / height < width / h) {
        pixmap.drawPixmap(
            this, 0, 0, this.width, this.height,
            0, floor((height - h) * 0.5f), width, ceil(h)
        )
    } else TODO()

    return pixmap
}