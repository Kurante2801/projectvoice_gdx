package com.kurante.projectvoice_gdx.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Pools
import com.kurante.projectvoice_gdx.util.UserInterface
import ktx.actors.onChange
import ktx.scene2d.KTable

class PVToggle(var value: Boolean) : Table(), KTable, MainColorElement {
    val yesButton: PVTextButton
    val noButton: PVTextButton
    val mainColorChanged: (Color) -> Unit

    init {
        yesButton = pvTextButton("") {
            it.grow()
            setMainColor(false)
            color = if (this@PVToggle.value) UserInterface.mainColor else UserInterface.FOREGROUND1_COLOR

            onChange {
                this@PVToggle.setValue(true)
            }
        }

        noButton = pvTextButton("") {
            it.grow()
            setMainColor(false)
            color = if (!this@PVToggle.value) UserInterface.mainColor else UserInterface.FOREGROUND1_COLOR

            onChange {
                this@PVToggle.setValue(false)
            }
        }

        mainColorChanged = { color: Color ->
            yesButton.clearActions()
            yesButton.addAction(Actions.color(if (value) color else UserInterface.FOREGROUND1_COLOR, 0.25f))
            noButton.clearActions()
            noButton.addAction(Actions.color(if (!value) color else UserInterface.FOREGROUND1_COLOR, 0.25f))
        }

        setMainColor(true)
    }


    override fun setMainColor(enabled: Boolean) {
        if (enabled) {
            UserInterface.mainColorEvent += this.mainColorChanged
            mainColorChanged(UserInterface.mainColor)
        } else
            UserInterface.mainColorEvent -= this.mainColorChanged
    }

    fun setValue(value: Boolean, fireEvent: Boolean = true) {
        if (value == this.value) return
        this.value = value

        if (fireEvent) {
            val changeEvent = Pools.obtain(ChangeListener.ChangeEvent::class.java)
            if (fire(changeEvent)) this.value = !this.value
            Pools.free(changeEvent)
        }

        mainColorChanged(UserInterface.mainColor)
    }
}