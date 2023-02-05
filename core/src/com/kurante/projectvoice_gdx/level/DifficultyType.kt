package com.kurante.projectvoice_gdx.level

import com.badlogic.gdx.graphics.Color

enum class DifficultyType(
    val color: Color
) {
    EASY(Color.valueOf("#32E1Af")),
    HARD(Color.valueOf("#E13232")),
    EXTRA(Color.valueOf("#FF4B00"))
}