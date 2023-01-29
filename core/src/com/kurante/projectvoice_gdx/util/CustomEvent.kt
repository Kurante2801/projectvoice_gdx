package com.kurante.projectvoice_gdx.util

class CustomEvent<T> {
    private val listeners = mutableSetOf<(T) -> Unit>()

    operator fun plusAssign(listener: (T) -> Unit) {
        listeners.add(listener)
    }

    operator fun minusAssign(listener: (T) -> Unit) {
        listeners.remove(listener)
    }

    operator fun invoke(value: T) {
        for (listener in listeners)
            listener.invoke(value)
    }
}