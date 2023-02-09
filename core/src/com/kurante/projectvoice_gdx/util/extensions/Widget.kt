package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.kurante.projectvoice_gdx.ui.PVImageTextButton
import com.kurante.projectvoice_gdx.util.UserInterface.FOREGROUND2_COLOR
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import ktx.scene2d.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// These functions basically call the ktx scene2d functions
// but styles the elements

@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.pvImageTextButton(
    text: String,
    drawable: Drawable? = null,
    init: (@Scene2dDsl PVImageTextButton).(S) -> Unit = {}
): PVImageTextButton {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(PVImageTextButton(text, Scene2DSkin.defaultSkin)) {
        if (drawable != null) {
            this.style = ImageTextButton.ImageTextButtonStyle(this.style).apply {
                imageUp = drawable
            }
        }

        init(this, it)
    }
}

@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.textField(
    text: String = "",
    init: (@Scene2dDsl TextField).(S) -> Unit = {}
): TextField {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(TextField(text, Scene2DSkin.defaultSkin, defaultStyle)) {
        if (it is Cell<*>) {
            it.prefSize(160f.scaledUi(), 48f.scaledUi())
            it.pad(8f.scaledUi())
        }

        this.color = FOREGROUND2_COLOR
        init(this, it)
    }
}