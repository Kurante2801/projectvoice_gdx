package com.kurante.projectvoice_gdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.kurante.projectvoice_gdx.util.UserInterface
import ktx.preferences.get
import ktx.preferences.set
import java.util.*

class PlayerPreferences(val game: ProjectVoice) {
    private val prefs: Preferences = Gdx.app.getPreferences("com.kurante.projectvoice_gdx")

    // REMINDER: You need to call prefs.flush after every set()

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
            prefs.flush()
        }

    var musicVolume: Float
        get() = prefs["musicVolume"] ?: 1f
        set(value) {
            prefs["musicVolume"] = value
            prefs.flush()
        }

    var safeArea: Boolean
        get() = prefs["safeArea"] ?: true
        set(value) {
            prefs["safeArea"] = value
            prefs.flush()
            game.resize(Gdx.graphics.width, Gdx.graphics.height)
        }

    var storageString: String?
        get() = prefs["storage"]
        set(value) {
            prefs["storage"] = value!!
            prefs.flush()
        }

    var noteSpeed: Int
        get() = prefs["noteIndex"] ?: 2
        set(value) {
            prefs["noteIndex"] = value
            prefs.flush()
        }
}