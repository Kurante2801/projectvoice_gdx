package com.kurante.projectvoice_gdx.util

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.kurante.projectvoice_gdx.PlayerPreferences
import ktx.preferences.get
import ktx.preferences.set
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class StringPreference(
    private val key: String,
    default: String,
    private val preferences: Preferences,
    private val onChange: ((String) -> Unit)? = null
) : ReadWriteProperty<PlayerPreferences, String> {
    private val cache = preferences[key] ?: default
    override fun getValue(thisRef: PlayerPreferences, property: KProperty<*>): String = cache
    override fun setValue(thisRef: PlayerPreferences, property: KProperty<*>, value: String) {
        onChange?.invoke(value)
        preferences[key] = value
        preferences.flush()
    }
}

class NullableStringPreference(
    private val key: String,
    default: String?,
    private val preferences: Preferences,
    private val onChange: ((String?) -> Unit)? = null
) : ReadWriteProperty<PlayerPreferences, String?> {
    private val cache = preferences[key] ?: default
    override fun getValue(thisRef: PlayerPreferences, property: KProperty<*>): String? = cache
    override fun setValue(thisRef: PlayerPreferences, property: KProperty<*>, value: String?) {
        onChange?.invoke(value)

        if (value == null) {
            preferences.remove(key)
        } else {
            preferences[key] = value
            preferences.flush()
        }
    }
}

class FloatPreference(
    private val key: String,
    default: Float,
    private val preferences: Preferences,
    private val onChange: ((Float) -> Unit)? = null
) : ReadWriteProperty<PlayerPreferences, Float> {
    private val cache = preferences[key] ?: default
    override fun getValue(thisRef: PlayerPreferences, property: KProperty<*>): Float = cache
    override fun setValue(thisRef: PlayerPreferences, property: KProperty<*>, value: Float) {
        onChange?.invoke(value)
        preferences[key] = value
        preferences.flush()
    }
}

class IntPreference(
    private val key: String,
    default: Int,
    private val preferences: Preferences,
    private val onChange: ((Int) -> Unit)? = null
) : ReadWriteProperty<PlayerPreferences, Int> {
    private val cache = preferences[key] ?: default
    override fun getValue(thisRef: PlayerPreferences, property: KProperty<*>): Int = cache
    override fun setValue(thisRef: PlayerPreferences, property: KProperty<*>, value: Int) {
        onChange?.invoke(value)
        preferences[key] = value
        preferences.flush()
    }
}

class BooleanPreference(
    private val key: String,
    default: Boolean,
    private val preferences: Preferences,
    private val onChange: ((Boolean) -> Unit)? = null
) : ReadWriteProperty<PlayerPreferences, Boolean> {
    private val cache = preferences[key] ?: default
    override fun getValue(thisRef: PlayerPreferences, property: KProperty<*>): Boolean = cache
    override fun setValue(thisRef: PlayerPreferences, property: KProperty<*>, value: Boolean) {
        onChange?.invoke(value)
        preferences[key] = value
        preferences.flush()
    }
}

class ColorPreference(
    private val key: String,
    default: String,
    private val preferences: Preferences,
    private val onChange: ((Color) -> Unit)? = null,
) : ReadWriteProperty<PlayerPreferences, Color> {
    private val cache = Color.valueOf(preferences[key] ?: default)
    override fun getValue(thisRef: PlayerPreferences, property: KProperty<*>): Color = cache
    override fun setValue(thisRef: PlayerPreferences, property: KProperty<*>, value: Color) {
        onChange?.invoke(value)
        cache.set(value)
        preferences[key] = value.toString()
        preferences.flush()
    }
}