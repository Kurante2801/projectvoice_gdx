package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion

fun Batch.draw(
    color: Color, region: TextureRegion,
    x: Float, y: Float, width: Float, height: Float,
) {
    this.color = color
    this.draw(region, x, y, width, height)
}