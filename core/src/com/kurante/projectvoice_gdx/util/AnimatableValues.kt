package com.kurante.projectvoice_gdx.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object AnimatableValues {
    open class Animatable(
        var target: Float,
        open var transitionTime: Float = 0.25f,
    ) {
        var value: Float = target

        fun act(delta: Float) {
            value = when {
                value == target -> return
                value > target -> (value - delta / transitionTime).coerceAtLeast(target)
                else -> (value + delta / transitionTime).coerceAtMost(target)
            }
        }
    }

    val animatables = mutableMapOf<String, Animatable>()

    fun act(delta: Float) {
        for (animatable in animatables.values)
            animatable.act(delta)
    }

    operator fun get(name: String) = animatables[name]

    fun remove(name: String) = animatables.remove(name)

    class FloatDelegate(
        name: String,
        initial: Float,
        override var transitionTime: Float = 0.25f,
    ) : Animatable(initial, transitionTime), ReadWriteProperty<Any, Float> {
        init {
            animatables[name] = this
        }

        override fun getValue(thisRef: Any, property: KProperty<*>): Float =
            value

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Float) {
            target = value
        }
    }
}

