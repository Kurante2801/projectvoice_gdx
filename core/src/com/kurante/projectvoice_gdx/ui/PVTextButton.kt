package com.kurante.projectvoice_gdx.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.I18NBundle
import com.kurante.projectvoice_gdx.util.UserInterface
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import com.kurante.projectvoice_gdx.util.extensions.onLocalizationChanged
import com.kurante.projectvoice_gdx.util.extensions.setLocalizedText
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin.defaultSkin
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.max

class PVTextButton(text: String) : TextButton(text, defaultSkin), MainColorElement {
    var mainColorChanged = { color: Color ->
        addAction(Actions.color(color, 0.25f))
    }

    init {
        style = defaultSkin.get(ImageTextButtonStyle::class.java)

        color = UserInterface.mainColor
        UserInterface.mainColorEvent += mainColorChanged
    }

    override fun getPrefWidth(): Float {
        return max(super.getPrefWidth(), 160f.scaledUi())
    }

    override fun getPrefHeight(): Float {
        return max(super.getPrefHeight(), 48f.scaledUi())
    }

    override fun setMainColor(enabled: Boolean) {
        if (enabled) {
            UserInterface.mainColorEvent += mainColorChanged
            color = UserInterface.mainColor
        } else {
            UserInterface.mainColorEvent -= mainColorChanged
            color = UserInterface.FOREGROUND1_COLOR
        }
    }

    override fun setDisabled(isDisabled: Boolean) {
        super.setDisabled(isDisabled)
        setMainColor(!isDisabled)
    }

    fun setLocalizedText(key: String) = label.setLocalizedText(key)
    fun onLocalizationChanged(callback: (I18NBundle) -> Unit) = label.onLocalizationChanged(callback)
}

@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.pvTextButton(
    text: String,
    init: (@Scene2dDsl PVTextButton).(S) -> Unit = {}
): PVTextButton {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(PVTextButton(text), init)
}