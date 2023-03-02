@file:Suppress("UNCHECKED_CAST")

package com.kurante.projectvoice_gdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.kurante.projectvoice_gdx.util.UserInterface
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

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
    var backgroundBlur by delegatedFloat("backgroundBlur", 2.75f)
    var backgroundOpacity by delegatedFloat("backgroundOpacity", 0.75f)

    // GAMEPLAY
    var noteSpeedIndex by delegatedInt("noteSpeedIndex", 6)

    var noteClickBackground by delegatedColor("clickBack", Color.valueOf("#000000")!!)
    var noteClickForeground by delegatedColor("clickFore", Color.valueOf("#DC4B4B")!!)

    var noteHoldBackground by delegatedColor("holdBack", Color.valueOf("#000000")!!)
    var noteHoldTopForeground by delegatedColor("holdTopFore", Color.valueOf("#DC4B4B")!!)
    var noteHoldBottomForeground by delegatedColor("holdBottomFore", Color.valueOf("#DC4B4B")!!)

    var noteSlideBackground by delegatedColor("slideBack", Color.valueOf("#000000")!!)
    var noteSlideForeground by delegatedColor("slideFore", Color.valueOf("#FFFFFF")!!)

    var noteSwipeLeftBackground by delegatedColor("swipeLeftBack", Color.valueOf("#000000")!!)
    var noteSwipeLeftForeground by delegatedColor("swipeLeftFore", Color.valueOf("#00FFFF")!!)

    var noteSwipeRightBackground by delegatedColor("swipeRightBack", Color.valueOf("#000000")!!)
    var noteSwipeRightForeground by delegatedColor("swipeRightFore", Color.valueOf("#00FFFF")!!)

    var noteTickBackground by delegatedColor("tickBack", Color.valueOf("#DC4B4B")!!)
    var noteTickForeground by delegatedColor("tickFore", Color.valueOf("#FFFFFF")!!)

    // The <T> is to allow nullables
    private fun <T : String?> delegatedString(key: String, default: T, onChange: (T) -> Unit = {}) : ReadWriteProperty<Any, T> {
        var cache = if (key in preferences) preferences.getString(key) as T else default
        return object : ReadWriteProperty<Any, T> {
            override fun getValue(thisRef: Any, property: KProperty<*>): T {
                return cache
            }

            override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
                if (value === cache) return
                cache = value
                onChange(value)

                if (value == null)
                    preferences.remove(key)
                else
                    preferences.putString(key, value as String)
                preferences.flush()
            }
        }
    }

    private fun <T : Boolean?> delegatedBoolean(key: String, default: T, onChange: (T) -> Unit = {}) : ReadWriteProperty<Any, T> {
        var cache = if (key in preferences) preferences.getBoolean(key) as T else default
        return object : ReadWriteProperty<Any, T> {
            override fun getValue(thisRef: Any, property: KProperty<*>): T {
                return cache
            }

            override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
                if (value === cache) return
                cache = value
                onChange(value)

                if (value == null)
                    preferences.remove(key)
                else
                    preferences.putBoolean(key, value as Boolean)
                preferences.flush()
            }
        }
    }

    private fun <T : Int?> delegatedInt(key: String, default: T, onChange: (T) -> Unit = {}) : ReadWriteProperty<Any, T> {
        var cache = if (key in preferences) preferences.getInteger(key) as T else default
        return object : ReadWriteProperty<Any, T> {
            override fun getValue(thisRef: Any, property: KProperty<*>): T {
                return cache
            }

            override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
                if (value === cache) return
                cache = value
                onChange(value)

                if (value == null)
                    preferences.remove(key)
                else
                    preferences.putInteger(key, value as Int)
                preferences.flush()
            }
        }
    }

    private fun <T : Float?> delegatedFloat(key: String, default: T, onChange: (T) -> Unit = {}) : ReadWriteProperty<Any, T> {
        var cache = if (key in preferences) preferences.getFloat(key) as T else default
        return object : ReadWriteProperty<Any, T> {
            override fun getValue(thisRef: Any, property: KProperty<*>): T {
                return cache
            }

            override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
                if (value === cache) return
                cache = value
                onChange(value)

                if (value == null)
                    preferences.remove(key)
                else
                    preferences.putFloat(key, value as Float)
                preferences.flush()
            }
        }
    }

    private fun <T : Color?> delegatedColor(key: String, default: T, onChange: (T) -> Unit = {}) : ReadWriteProperty<Any, T> {
        var cache = if (key in preferences) Color.valueOf(preferences.getString(key)) as T else default
        return object : ReadWriteProperty<Any, T> {
            override fun getValue(thisRef: Any, property: KProperty<*>): T {
                return cache
            }

            override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
                if (value === cache) return
                cache = value
                onChange(value)

                if (value == null)
                    preferences.remove(key)
                else
                    preferences.putString(key, (value as Color).toString())
                preferences.flush()
            }
        }
    }
}