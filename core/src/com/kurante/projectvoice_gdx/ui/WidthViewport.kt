package com.kurante.projectvoice_gdx.ui

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils.log2
import com.badlogic.gdx.utils.viewport.Viewport
import kotlin.math.pow

/**
 * Scales according to window width only.
 */
class WidthViewport : Viewport() {
    init {
        if (camera == null)
            camera = OrthographicCamera()
    }

    companion object {
        // Viewport is scaled down so textures don't look blurry (such as buttons)
        const val REFERENCE_WIDTH = 2460f

        const val UI_SCALE = 1280f / REFERENCE_WIDTH
        /**
         * Game is developed on a 1280x980 window. This function
         * converts the Float to match REFERENCE_WIDTH, which is much larger.
         * On a window with 1280 pixels of width, this will be pixel perfect (in theory)
         */
        fun Float.scaledUi() = this / UI_SCALE
    }

    override fun update(screenWidth: Int, screenHeight: Int, centerCamera: Boolean) {
        val scaleFactor = 2f.pow(log2(screenWidth / REFERENCE_WIDTH))
        setWorldSize(screenWidth /scaleFactor, screenHeight / scaleFactor)

        setScreenBounds(0, 0, screenWidth, screenHeight)
        apply(centerCamera)
    }
}