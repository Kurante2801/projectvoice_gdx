package com.kurante.projectvoice_gdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.kurante.projectvoice_gdx.util.*
import com.kurante.projectvoice_gdx.util.extensions.withAlpha
import ktx.preferences.get
import ktx.preferences.set

class PlayerPreferences(val game: ProjectVoice) {
    val preferences: Preferences = Gdx.app.getPreferences("com.kurante.projectvoice_gdx")

    val locales = mutableMapOf<String, String>()
    var locale by delegatedString("locale", "en") {
        if (it !in locales.keys)
            Gdx.app.error("PlayerPreferences", "Tried to set an invalid language: $it")
        else
            UserInterface.setLocale(it)
    }

    var musicVolume by delegatedFloat("musicVolume", 1f) // TODO: Change volume if playing in level summary

    var safeArea by delegatedBoolean("safeArea", true) {
        game.resize(Gdx.graphics.width, Gdx.graphics.height)
    }

    var storageString by delegatedString<String?>("storage", null)

    // GAMEPLAY
    var noteSpeedIndex by delegatedInt("noteSpeedIndex", 6)

    var noteClickBackground by delegatedColor("clickBack", Color.valueOf("#000000"))
    var noteClickForeground by delegatedColor("clickFore", Color.valueOf("#DC4B4B"))

    var noteHoldBackground by delegatedColor("holdBack", Color.valueOf("#000000"))
    var noteHoldTopForeground by delegatedColor("holdTopFore", Color.valueOf("#DC4B4B"))
    var noteHoldBottomForeground by delegatedColor("holdBottomFore", Color.valueOf("#DC4B4B"))

    var noteSlideBackground by delegatedColor("slideBack", Color.valueOf("#000000"))
    var noteSlideForeground by delegatedColor("slideFore", Color.valueOf("#FFFFFF"))

    var noteSwipeLeftBackground by delegatedColor("swipeLeftBack", Color.valueOf("#000000"))
    var noteSwipeLeftForeground by delegatedColor("swipeLeftFore", Color.valueOf("#00FFFF"))

    var noteSwipeRightBackground by delegatedColor("swipeRightBack", Color.valueOf("#000000"))
    var noteSwipeRightForeground by delegatedColor("swipeRightFore", Color.valueOf("#00FFFF"))
}