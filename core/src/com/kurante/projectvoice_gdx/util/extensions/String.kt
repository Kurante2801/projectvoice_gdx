package com.kurante.projectvoice_gdx.util.extensions

fun String.lastIndexOfOrNull(
    char: Char, startIndex: Int = lastIndex, ignoreCase: Boolean = false
): Int? {
    val index = lastIndexOf(char, startIndex, ignoreCase)
    return if (index == -1) null else index
}

fun String.lastIndexOfOrNull(
    string: String, startIndex: Int = lastIndex, ignoreCase: Boolean = false
): Int? {
    val index = lastIndexOf(string, startIndex, ignoreCase)
    return if (index == -1) null else index
}

fun String.capitalize() =
    this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.ROOT) else it.toString() }