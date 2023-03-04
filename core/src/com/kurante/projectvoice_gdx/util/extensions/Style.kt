package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle

fun LabelStyle.withFont(font: BitmapFont) = LabelStyle(this).apply {
    this.font = font
}

fun TextButtonStyle.withFont(font: BitmapFont) = TextButtonStyle(this).apply {
    this.font = font
}