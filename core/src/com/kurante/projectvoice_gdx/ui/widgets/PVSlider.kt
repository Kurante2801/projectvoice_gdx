package com.kurante.projectvoice_gdx.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisSlider
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import de.tomgrill.gdxdialogs.core.GDXDialogs
import de.tomgrill.gdxdialogs.core.dialogs.GDXTextPrompt
import de.tomgrill.gdxdialogs.core.listener.TextPromptListener
import ktx.actors.onChange
import ktx.actors.onChangeEvent
import ktx.scene2d.*
import ktx.scene2d.vis.visSlider
import java.text.DecimalFormat
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class PVSlider(
    var value: Float,
    val min: Float,
    val max: Float,
    val step: Float = 0.1f,
    val dialogs: GDXDialogs,
) : Table(), KTable {
    val formatter = DecimalFormat("0.##")
    val button: PVTextButton
    val slider: VisSlider
    init {
        setFillParent(true)

        table {
            it.growX()
            align(Align.left)

            this@PVSlider.button = pvTextButton("") {
                setMainColor(false)
                it.width(100f.scaledUi())
                it.pad(0f, 0f, 0f, 8f.scaledUi())
            }

            this@PVSlider.slider = visSlider(this@PVSlider.min, this@PVSlider.max, this@PVSlider.step) {
                value = this@PVSlider.value
                it.minWidth(500f)

                onChangeEvent {
                    this@PVSlider.value = this.value
                    this@PVSlider.updateValue()
                }
            }
        }

        button.onChange {
            val dialog = this@PVSlider.dialogs.newDialog(GDXTextPrompt::class.java)
            dialog.setTitle("Number Field")
            dialog.setMessage("Please select a number between ${formatter.format(slider.minValue)} and ${formatter.format(slider.maxValue)}")
            dialog.setCancelButtonLabel("Cancel")
            dialog.setConfirmButtonLabel("Confirm")
            dialog.setTextPromptListener(object : TextPromptListener {
                override fun cancel() {}

                override fun confirm(text: String) {
                    val value = text.toFloatOrNull()
                    if (value != null)
                        this@PVSlider.slider.value = value
                }
            })

            dialog.build().show()
        }

        updateValue()
    }

    fun updateValue() {
        button.setText(DecimalFormat("0.##").format(value))
        slider.value = value // This does NOT cause a stack overflow
    }
}