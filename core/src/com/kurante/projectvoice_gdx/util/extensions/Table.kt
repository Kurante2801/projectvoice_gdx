package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Table

fun Table.padInset(enabled: Boolean) {
    if (enabled) {
        this.pad(
            Gdx.graphics.safeInsetTop.toFloat(),
            Gdx.graphics.safeInsetLeft.toFloat(),
            Gdx.graphics.safeInsetBottom.toFloat(),
            Gdx.graphics.safeInsetRight.toFloat(),
        )
    } else
        this.pad(0f)

    invalidate()
}