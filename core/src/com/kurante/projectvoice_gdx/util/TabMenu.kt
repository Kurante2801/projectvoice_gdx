package com.kurante.projectvoice_gdx.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.MathUtils.lerp
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.kurante.projectvoice_gdx.ui.MainColorElement
import com.kurante.projectvoice_gdx.ui.PVImageTextButton
import com.kurante.projectvoice_gdx.ui.PVTextButton
import com.kurante.projectvoice_gdx.ui.pvTextButton
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import com.kurante.projectvoice_gdx.util.extensions.pvImageTextButton
import ktx.actors.onChange
import ktx.graphics.color
import ktx.graphics.use
import ktx.scene2d.*
import java.time.temporal.Temporal

class TabMenu(
    val canToggle: Boolean = true
) : Table(), KTable {
    data class Tab(
        val button: Button,
        val content: Actor,
        var active: Boolean,
        var opacity: Float,
    )

    private class ChangeAction(
        private val active: Boolean,
        private val tab: Tab,
    ) : TemporalAction() {
        private val initialOpacity = tab.opacity

        init {
            duration = 0.25f
            tab.active = true
            tab.content.touchable = Touchable.disabled

            tab.button.addAction(
                Actions.color(
                    if (active)
                        UserInterface.mainColor
                    else
                        UserInterface.FOREGROUND1_COLOR, 0.25f
                ),
            )

            tab.content.addAction(Actions.sequence(
                Actions.delay(0.25f),
                Actions.run {
                    tab.content.touchable = if (active) Touchable.enabled else Touchable.disabled
                    tab.active = active
                }
            ))
        }

        override fun update(percent: Float) {
            tab.opacity = lerp(initialOpacity, if (active) 1f else 0f, percent)
        }
    }

    val tabs = mutableListOf<Tab>()
    val tabGroup: HorizontalGroup
    val contentContainer = TabContentContainer(this)

    init {
        setFillParent(true)

        tabGroup = horizontalGroup {
            align(Align.center)
            space(28f.scaledUi())

            it.growX()
            it.pad(28f.scaledUi())
        }

        defaults().row()

        add(contentContainer).apply {
            grow()
            pad(0f, 28f.scaledUi(), 28f.scaledUi(), 28f.scaledUi())
        }
    }

    fun addTab(text: String, content: Actor) {
        addTab(PVTextButton(text), content)
    }

    fun addTab(button: Button, content: Actor) {
        val active = tabs.isEmpty() && !canToggle

        content.touchable = if (active) Touchable.enabled else Touchable.disabled

        if (button is MainColorElement)
            button.setMainColor(false)

        button.color = if (active) UserInterface.mainColor else UserInterface.FOREGROUND1_COLOR

        contentContainer.addActor(content)
        tabGroup.addActor(button)

        val tab = Tab(button, content, active, if (active) 1f else 0f)
        button.onChange {
            if (tab.active && !canToggle) return@onChange

            for (otherTab in tabs) {
                if(otherTab == tab)
                    addAction(ChangeAction(!tab.active, otherTab))
                else
                    addAction(ChangeAction(false, otherTab))
            }
        }

        tabs.add(tab)
    }
}

class TabContentContainer(private val menu: TabMenu) : WidgetGroup() {
    override fun drawChildren(batch: Batch, parentAlpha: Float) {
        for (tab in menu.tabs) {
            if (tab.active) {
                tab.content.draw(batch, tab.opacity)
            }
        }
    }
}