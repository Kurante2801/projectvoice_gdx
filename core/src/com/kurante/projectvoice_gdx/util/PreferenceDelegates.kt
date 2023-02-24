package com.kurante.projectvoice_gdx.util

import com.badlogic.gdx.graphics.Color
import com.kurante.projectvoice_gdx.PlayerPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

// The <T> is to allow nullables
fun <T : String?> PlayerPreferences.delegatedString(key: String, default: T, onChange: (T) -> Unit = {}) : ReadWriteProperty<Any, T> {
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

fun <T : Boolean?> PlayerPreferences.delegatedBoolean(key: String, default: T, onChange: (T) -> Unit = {}) : ReadWriteProperty<Any, T> {
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

fun <T : Int?> PlayerPreferences.delegatedInt(key: String, default: T, onChange: (T) -> Unit = {}) : ReadWriteProperty<Any, T> {
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

fun <T : Float?> PlayerPreferences.delegatedFloat(key: String, default: T, onChange: (T) -> Unit = {}) : ReadWriteProperty<Any, T> {
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

fun <T : Color?> PlayerPreferences.delegatedColor(key: String, default: T, onChange: (T) -> Unit = {}) : ReadWriteProperty<Any, T> {
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