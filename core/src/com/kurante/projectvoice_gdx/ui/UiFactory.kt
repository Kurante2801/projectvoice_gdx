package com.kurante.projectvoice_gdx.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.kurante.projectvoice_gdx.ui.UiUtil.FOREGROUND2_COLOR
import com.kurante.projectvoice_gdx.ui.UiUtil.scaledUi
import ktx.scene2d.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun Actor.setMainColor(duration: Float = 0.25f) {
    this.color = UiUtil.mainColor
    UiUtil.mainColorEvent += { this.addAction(Actions.color(it, duration)) }
}

// These functions basically call the ktx scene2d functions
// but styles the elements

@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.textButton(
    text: String,
    init: (@Scene2dDsl KTextButton).(S) -> Unit = {}
): KTextButton {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(KTextButton(text, Scene2DSkin.defaultSkin, defaultStyle)) {
        (it as Cell<*>).prefSize(160f.scaledUi(), 48f.scaledUi())
        this.pad(8f.scaledUi())
        this.setMainColor()

        init(this, it)
    }
}

@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.imageTextButton(
    text: String,
    drawable: Drawable? = null,
    init: (@Scene2dDsl KImageTextButton).(S) -> Unit = {}
): KImageTextButton {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(KImageTextButton(text, Scene2DSkin.defaultSkin, defaultStyle)) {
        (it as Cell<*>).prefSize(160f.scaledUi(), 48f.scaledUi())
        this.pad(8f.scaledUi())
        this.setMainColor()

        if(drawable != null)
            this.style.imageUp = drawable

        init(this, it)
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
    return actor(PVImageTextButton(text, Scene2DSkin.defaultSkin)) {
        (it as Cell<*>).prefSize(160f.scaledUi(), 48f.scaledUi())

        if(drawable != null) {
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
        val cell = it as Cell<*>
        cell.prefSize(160f.scaledUi(), 48f.scaledUi())
        cell.pad(8f.scaledUi())
        this.color = FOREGROUND2_COLOR
        init(this, it)
    }
}

