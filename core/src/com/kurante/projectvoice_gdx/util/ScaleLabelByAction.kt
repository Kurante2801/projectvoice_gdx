package com.kurante.projectvoice_gdx.util

import com.badlogic.gdx.scenes.scene2d.actions.RelativeTemporalAction
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.kurante.projectvoice_gdx.util.extensions.scaleFontBy

class ScaleLabelByAction : RelativeTemporalAction() {
    var amountX = 0f
    var amountY = 0f
    override fun updateRelative(percentDelta: Float) {
        (target as Label).scaleFontBy(amountX * percentDelta, amountY * percentDelta)
    }

    fun setAmount(x: Float, y: Float) {
        amountX = x
        amountY = y
    }

    fun setAmount(scale: Float) {
        amountX = scale
        amountY = scale
    }
}