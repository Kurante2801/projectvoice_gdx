package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle

fun LabelStyle.withFont(font: BitmapFont) = LabelStyle(this).apply {
    this.font = font
}