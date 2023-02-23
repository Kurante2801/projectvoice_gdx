package com.kurante.projectvoice_gdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.kurante.projectvoice_gdx.util.*
import com.kurante.projectvoice_gdx.util.extensions.withAlpha
import ktx.preferences.get
import ktx.preferences.set

class PlayerPreferences(val game: ProjectVoice) {
    private val preferences: Preferences = Gdx.app.getPreferences("com.kurante.projectvoice_gdx")

    val locales = mutableMapOf<String, String>()
    var locale by StringPreference("locale", "en", preferences) {
        if (it !in locales.keys)
            Gdx.app.error("PlayerPreferences", "Tried to set an invalid language: $it")
    }

    var musicVolume by FloatPreference("musicVolume", 1f, preferences) // TODO: Change volume if playing in level summary

    var safeArea by BooleanPreference("safeArea", true, preferences) {
        game.resize(Gdx.graphics.width, Gdx.graphics.height)
    }

    var storageString by NullableStringPreference("storage", null, preferences)

    // GAMEPLAY
    var noteSpeedIndex by IntPreference("noteSpeedIndex", 6, preferences)

    var noteClickBackground by ColorPreference("clickBack", "#000000", preferences)
    var noteClickForeground by ColorPreference("clickFore", "#DC4B4B", preferences)

    var noteHoldBackground by ColorPreference("holdBack", "#000000", preferences)
    var noteHoldTopForeground by ColorPreference("holdTopFore", "#DC4B4B", preferences)
    var noteHoldBottomForeground by ColorPreference("holdBottomFore", "#DC4B4B", preferences)

    var noteSlideBackground by ColorPreference("slideBack", "#000000", preferences)
    var noteSlideForeground by ColorPreference("slideFore", "#FFFFFF", preferences)

    var noteSwipeLeftBackground by ColorPreference("swipeLeftBack", "#000000", preferences)
    var noteSwipeLeftForeground by ColorPreference("swipeLeftFore", "#00FFFF", preferences)

    var noteSwipeRightBackground by ColorPreference("swipeRightBack", "#000000", preferences)
    var noteSwipeRightForeground by ColorPreference("swipeRightFore", "#00FFFF", preferences)
}