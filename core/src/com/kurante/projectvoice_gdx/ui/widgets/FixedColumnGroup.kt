package com.kurante.projectvoice_gdx.ui.widgets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import ktx.scene2d.KGroup
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor
import java.lang.Float.max
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Fixed column grid with adjustable child aspect ratio
 */
class FixedColumnGroup : WidgetGroup(), KGroup {
    var spacing = 28f.scaledUi()

    var columns = 3
    var aspectRatio: Float = 16f / 9f

    @get:JvmName("getPreferredWidth")
    var prefWidth = 0f
    @get:JvmName("getPreferredHeight")
    var prefHeight = 0f

    private var sizeInvalid = true
    private var childWidth = 0f
    private var childHeight = 0f
    private var lastPrefHeight = -1f

    init {
        touchable = Touchable.childrenOnly
    }

    private fun computeSize() {
        prefWidth = width
        prefHeight = 0f
        sizeInvalid = false

        childWidth = (width - spacing * columns) / columns
        childHeight = childWidth / aspectRatio

        children.forEachIndexed { i, child ->
            child.width = childWidth
            child.height = childHeight

            if ((i + 1) % columns == 0 || i == children.size - 1)
                prefHeight += childHeight + spacing

            if (i == children.size - 1)
                prefHeight -= spacing
        }
    }

    override fun layout() {
        if (sizeInvalid) {
            computeSize()
            if (lastPrefHeight != prefHeight) {
                lastPrefHeight = prefHeight
                invalidateHierarchy()
            }
        }

        var x = 0f
        var y = height - childHeight

        for (child in children) {
            if (x + childWidth + spacing > width) {
                x = 0f
                y -= childHeight + spacing
            }

            child.setBounds(x, y, childWidth, childHeight)
            x += childWidth + spacing
        }
    }

    override fun invalidate() {
        super.invalidate()
        sizeInvalid = true
    }

    override fun getPrefWidth() = prefWidth
    override fun getPrefHeight() = prefHeight
}

@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.fixedColumnGroup(
    columns: Int,
    init: (@Scene2dDsl FixedColumnGroup).(S) -> Unit = {}
): FixedColumnGroup {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(FixedColumnGroup().apply { this.columns = columns }, init)
}