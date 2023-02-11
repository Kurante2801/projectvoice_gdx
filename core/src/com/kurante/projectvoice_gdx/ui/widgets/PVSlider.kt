package com.kurante.projectvoice_gdx.ui.widgets

import com.badlogic.gdx.math.MathUtils.round
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Pools
import com.kotcrab.vis.ui.Sizes
import com.kotcrab.vis.ui.widget.VisValidatableTextField
import com.kotcrab.vis.ui.widget.color.ColorPicker
import com.kotcrab.vis.ui.widget.spinner.FloatSpinnerModel
import com.kotcrab.vis.ui.widget.spinner.Spinner
import com.kotcrab.vis.ui.widget.spinner.Spinner.SpinnerStyle
import com.kurante.projectvoice_gdx.util.UserInterface
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import ktx.actors.onChange
import ktx.actors.onChangeEvent
import ktx.scene2d.KTable
import ktx.scene2d.Scene2DSkin.defaultSkin
import ktx.scene2d.container
import ktx.scene2d.defaultStyle
import ktx.scene2d.textField
import ktx.scene2d.vis.spinner
import ktx.scene2d.vis.visSlider
import ktx.scene2d.vis.visValidatableTextField
import java.text.DecimalFormat

class PVSlider(
    var value: Float,
    val min: Float,
    val max: Float,
    val step: Float = 0.1f,
) : Table(), KTable {

    val text = PVTextButton("")
    init {
        setFillParent(true)
        debug = true

        text.setMainColor(false)

        add(text).apply {
            width(100f.scaledUi())
            pad(0f, 0f, 0f, 8f.scaledUi())
        }

        container {
            it.growX()
            align(Align.left)
            minWidth(300f.scaledUi())

            visSlider(this@PVSlider.min, this@PVSlider.max, this@PVSlider.step) {
                onChangeEvent {
                    this@PVSlider.value = this.value
                    this@PVSlider.updateValue()
                }
            }
        }

        updateValue()
    }

    fun updateValue() {
        text.setText(DecimalFormat("0.##").format(value))
    }

}