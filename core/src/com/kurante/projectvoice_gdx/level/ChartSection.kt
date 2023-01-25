package com.kurante.projectvoice_gdx.level

data class ChartSection(
    val chartFilename: String,
    val difficulty: Int,
    val type: DifficultyType,
    val musicOverride: String? = null,
)