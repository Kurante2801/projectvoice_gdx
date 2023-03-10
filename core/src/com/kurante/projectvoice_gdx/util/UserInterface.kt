package com.kurante.projectvoice_gdx.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.I18NBundle
import com.kurante.projectvoice_gdx.ProjectVoice
import java.util.*

object UserInterface {
    const val UI_SCALE = 1280f / WidthViewport.REFERENCE_WIDTH
    const val REFERENCE_HEIGHT = 1845f

    /**
     * Game is developed on a 1280x980 window, but the viewport is larger.
     * This function converts the Float to match the viewport
     * (on a window with a width of 1280 pixels, an input value of X will be X visually.
     */
    fun Float.scaledUi(): Float = this / UI_SCALE
    fun Int.scaledUi(): Float = this / UI_SCALE
    fun Float.scaledStageX() = this.scaledUi() / WidthViewport.REFERENCE_WIDTH * ProjectVoice.stageWidth
    fun Float.scaledStageY() = this * REFERENCE_HEIGHT / ProjectVoice.stageHeight
    fun Float.scaledScreenY() = this / 960f * Gdx.graphics.height


    // Java interop
    fun scaledUI(value: Float) = value / UI_SCALE


    val mainColorEvent = CustomEvent<Color>()
    var mainColor: Color = Color.valueOf("#FF4B00")
        set(value) {
            field = value
            mainColorEvent.invoke(value)
        }

    val BACKGROUND_COLOR = Color.valueOf("#000000")!!
    val FOREGROUND1_COLOR = Color.valueOf("#323232")!!
    val FOREGROUND2_COLOR = Color.valueOf("#4B4B4B")!!

    // Locale stuff
    lateinit var lang: I18NBundle
    val localeEvent = CustomEvent<I18NBundle>()

    fun setLocale(locale: String?) {
        val handle = Gdx.files.internal("i18n/locale")
        // English is the default locale
        lang = if(locale == null)
            I18NBundle.createBundle(handle, "UTF-8")
        else
            I18NBundle.createBundle(handle, Locale(locale), "UTF-8")
        localeEvent.invoke(lang)
    }
}