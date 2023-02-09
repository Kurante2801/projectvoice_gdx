package com.kurante.projectvoice_gdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.kurante.projectvoice_gdx.util.UserInterface
import ktx.preferences.get
import ktx.preferences.set
import java.util.*

object PlayerPreferences {
    private val prefs: Preferences = Gdx.app.getPreferences("com.kurante.projectvoice_gdx")

    val locales = mutableMapOf<String, String>()
    var locale: String
        get() = prefs["locale"] ?: "en"
        set(value) {
            if(value !in locales.keys) {
                Gdx.app.error("PlayerPreferences", "Tried to set an invalid language: $value")
                return
            }

            prefs["locale"] = value
            UserInterface.setLocale(value)
        }
}