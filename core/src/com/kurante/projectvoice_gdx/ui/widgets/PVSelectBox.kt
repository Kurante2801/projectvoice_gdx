package com.kurante.projectvoice_gdx.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.utils.Array
import com.kurante.projectvoice_gdx.ui.widgets.PVSelectBox.SelectionElement
import com.kurante.projectvoice_gdx.util.UserInterface
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin.defaultSkin
import ktx.scene2d.Scene2dDsl
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.max

class PVSelectBox : SelectBox<SelectionElement>(defaultSkin) {
    class SelectionElement(private val text: String, val data: Any) {
        override fun toString() = text
    }

    init {
        color = UserInterface.FOREGROUND1_COLOR
        scrollPane.list.color = UserInterface.FOREGROUND2_COLOR
    }

    override fun getMinWidth(): Float {
        return max(super.getMinWidth(), 220f.scaledUi())
    }

    override fun getMinHeight(): Float {
        return max(super.getMinHeight(), 48f.scaledUi())
    }

    fun refreshItems() {
        items = Array(items)
    }

    fun addChoice(text: String, data: Any) {
        items.add(SelectionElement(text, data))
    }
}

@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun KWidget<*>.pvSelectBox(
    init: PVSelectBox.() -> Unit = {}
): PVSelectBox {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }

    val selectBox = PVSelectBox()
    storeActor(selectBox)
    selectBox.init()
    selectBox.refreshItems()
    return selectBox
}