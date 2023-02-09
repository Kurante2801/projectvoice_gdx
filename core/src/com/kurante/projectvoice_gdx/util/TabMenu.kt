package com.kurante.projectvoice_gdx.util

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils.lerp
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import com.kurante.projectvoice_gdx.ui.widgets.MainColorElement
import com.kurante.projectvoice_gdx.ui.widgets.PVImageTextButton
import com.kurante.projectvoice_gdx.ui.widgets.PVTextButton
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import ktx.actors.onChange
import ktx.scene2d.*
import ktx.scene2d.Scene2DSkin.defaultSkin

class TabMenu(
    val canToggle: Boolean = false
) : Table(), KTable, MainColorElement {
    companion object {
        const val FADE_TIME = 0.25f
    }

    data class Tab(
        val button: Button,
        val content: Actor,
        var active: Boolean,
        var opacity: Float,
    )

    class TabContentContainer(private val menu: TabMenu) : WidgetGroup() {
        override fun drawChildren(batch: Batch?, parentAlpha: Float) {
            for (tab in menu.tabs) {
                if (tab.opacity > 0f) {
                    tab.content.draw(batch, tab.opacity)
                }
            }
        }
    }

    /**
     * Makes the content drawable, fades it in/out, then hides it if fading out.
     */
    class ChangeAction(
        private val active: Boolean,
        private val tab: Tab,
    ) : TemporalAction() {
        private val initialOpacity = tab.opacity
        private val finalOpacity = if(active) 1f else 0f

        init {
            tab.button.clearActions()
            tab.content.clearActions()

            tab.active = active
            duration = FADE_TIME
            tab.content.touchable = Touchable.disabled // so user can't interact while fading

            val color = if (active) UserInterface.mainColor else UserInterface.FOREGROUND1_COLOR
            tab.button.addAction(Actions.color(color, FADE_TIME))

            tab.content.addAction(Actions.sequence(
                Actions.delay(FADE_TIME),
                Actions.run {
                    tab.content.touchable = if (active) Touchable.enabled else Touchable.disabled
                }
            ))
        }

        override fun update(percent: Float) {
            tab.opacity = lerp(initialOpacity, finalOpacity, percent)
        }
    }

    val tabs = mutableListOf<Tab>()
    val tabGroup: HorizontalGroup
    val contentContainer = TabContentContainer(this)

    val mainColorChanged = { color: Color ->
        for (tab in tabs) {
            if(tab.active) {
                tab.button.clearActions()
                tab.button.color = color
            }
        }
    }

    init {
        setFillParent(true)
        setMainColor(true)

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

    fun addTabLocalized(key: String, content: Actor) {
        val button = PVTextButton(key).apply {
            setLocalizedText(key)
        }
        addTab(button, content)
    }

    fun addTab(text: String, image: String, content: Actor) {
        addTab(text, defaultSkin.getDrawable(image), content)
    }

    fun addTabLocalized(key: String, image: String, content: Actor) {
        val button = PVTextButton(key).apply {
            setLocalizedText(key)
            style = ImageTextButtonStyle(style).apply {
                imageUp = defaultSkin.getDrawable(image)
            }
        }
        addTab(button, content)
    }

    fun addTab(text: String, drawable: Drawable, content: Actor) {
        val button = PVImageTextButton(
            text,
            defaultSkin
        )
        button.style = ImageTextButtonStyle(button.style).apply {
            imageUp = drawable
        }
        addTab(button, content)
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

            clearActions()
            val actions = Actions.parallel()

            for (otherTab in tabs) {
                if (otherTab == tab)
                    actions.addAction(ChangeAction(!tab.active, otherTab))
                else
                    actions.addAction(ChangeAction(false, otherTab))
            }

            addAction(actions)
        }

        tabs.add(tab)
    }

    override fun setMainColor(enabled: Boolean) {
        if (enabled) {
            UserInterface.mainColorEvent += mainColorChanged
            mainColorChanged(UserInterface.mainColor)
        } else
            UserInterface.mainColorEvent -= mainColorChanged
    }
}