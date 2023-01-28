package com.kurante.projectvoice_gdx.util

import com.badlogic.gdx.graphics.Color

object UserInterface {
    const val UI_SCALE = 1280f / WidthViewport.REFERENCE_WIDTH
    /**
     * Game is developed on a 1280x980 window, but the viewport is larger.
     * This function converts the Float to match the viewport
     * (on a window with a width of 1280 pixels, an input value of X will be X visually.
     */
    fun Float.scaledUi() = this / UI_SCALE

    val mainColorEvent = CustomEvent<Color>()
    var mainColor: Color = Color.valueOf("#FF4B00")
        set(value) {
            field = value
            mainColorEvent.invoke(value)
        }

    val BACKGROUND_COLOR: Color = Color.valueOf("#191919")
    val FOREGROUND1_COLOR: Color = Color.valueOf("#323232")
    val FOREGROUND2_COLOR: Color = Color.valueOf("#4B4B4B")
}