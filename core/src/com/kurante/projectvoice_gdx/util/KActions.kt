package com.kurante.projectvoice_gdx.util

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions

object KActions {
    fun scaleLabelBy(
        amountX: Float,
        amountY: Float,
        duration: Float = 0f,
        interpolation: Interpolation? = null,
    ) = Actions.action(ScaleLabelByAction::class.java).apply {
        this.amountX = amountX
        this.amountY = amountY
        this.duration = duration
        this.interpolation = interpolation
    }
}