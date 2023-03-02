package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.I18NBundle
import com.kurante.projectvoice_gdx.util.UserInterface

/**
 * Invokes callback once and registers it to be called again when the localization changes
 */
fun Label.onLocalizationChanged(callback: (I18NBundle) -> Unit) {
    callback.invoke(UserInterface.lang)
    UserInterface.localeEvent += callback
}

fun Label.setLocalizedText(key: String) {
    setText(UserInterface.lang[key])
    onLocalizationChanged { bundle ->
        setText(bundle[key])
    }
}

fun Label.scaleBy(scaleX: Float, scaleY: Float) {

}