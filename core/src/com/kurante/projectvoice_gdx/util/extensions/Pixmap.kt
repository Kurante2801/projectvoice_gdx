package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import java.lang.Integer.max
import java.lang.Integer.min

fun Pixmap.parseNinePatch(): NinePatch {
    fun get(isHeight: Boolean): Pair<Int, Int> {
        val until = if (isHeight) height else width
        var start = until - 1; var end = 0
        for (i in 1 until until) {
            val pixel = if (isHeight) getPixel(0, i) else getPixel(i, 0)
            if (pixel == 255) {
                start = min(start, i)
                end = max(end, i)
            }
        }
        start -= 1
        end = height - end - 2
        return Pair(start, end)
    }

    // Crop pixmap
    val new = Pixmap(width - 2, height - 2, format)
    new.drawPixmap(this, 0, 0, 1, 1, width, height)
    val tex = Texture(new)
    new.dispose()

    val (left, right) = get(false)
    val (top, bottom) = get(true)

    return NinePatch(tex, left, right, top, bottom)
}

