package com.kurante.projectvoice_gdx.game

import com.badlogic.gdx.graphics.Color
import com.kurante.projectvoice_gdx.util.extensions.mapRange
import kotlin.math.abs


data class Chart(
    val startTime: Int = 0,
    val endTime: Int?,
    val musicOffset: Int = 0,
    val tracks: Array<Track> = arrayOf()
)
