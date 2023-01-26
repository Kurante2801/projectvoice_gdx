package com.kurante.projectvoice_gdx.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.kurante.projectvoice_gdx.ui.UiUtil.FOREGROUND1_COLOR
import com.kurante.projectvoice_gdx.ui.UiUtil.scaledUi
import ktx.scene2d.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class PVImageTextButton(text: String) : ImageTextButton(text, Scene2DSkin.defaultSkin) {
    private val mainColor: (Color) -> Unit = {
        this.addAction(Actions.color(it, 0.25f))
    }

    init {
        this.color = UiUtil.mainColor
        UiUtil.mainColorEvent += this.mainColor
        pad(8f.scaledUi())
    }

    override fun setDisabled(disabled: Boolean) {
        super.setDisabled(disabled)
        if(disabled) {
            UiUtil.mainColorEvent -= mainColor
            this.color = FOREGROUND1_COLOR
        } else {
            UiUtil.mainColorEvent += mainColor
            this.color = UiUtil.mainColor
        }
    }
}

@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.pvImageTextButton(
    text: String,
    drawable: Drawable? = null,
    init: (@Scene2dDsl PVImageTextButton).(S) -> Unit = {}
): PVImageTextButton {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(PVImageTextButton(text)) {
        (it as Cell<*>).prefSize(160f.scaledUi(), 48f.scaledUi())

        if(drawable != null) {
            this.style = ImageTextButton.ImageTextButtonStyle(this.style).apply {
                imageUp = drawable
            }
        }

        init(this, it)
    }
}