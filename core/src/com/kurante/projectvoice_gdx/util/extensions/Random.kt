package com.kurante.projectvoice_gdx.util.extensions

import java.util.*
import kotlin.math.max

// Kotlin's random always returns the same values each application restart
fun nextInt(size: Int): Int = Random(System.currentTimeMillis()).nextInt(max(1, size))

fun <T> Array<out T>.randomOrNull(): T? {
    return elementAtOrNull(nextInt(size))
}

fun <T> Array<out T>.random(): T {
    if (isEmpty())
        throw NoSuchElementException("Array is empty.")
    return elementAt(nextInt(size))
}

fun CharArray.randomOrNull(): Char? {
    return elementAtOrNull(nextInt(size))
}

fun CharArray.random(): Char {
    if (isEmpty())
        throw NoSuchElementException("Array is empty.")
    return elementAt(nextInt(size))
}
