package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.imageTextButton
import com.kurante.projectvoice_gdx.ui.UiUtil.mainColor
import com.kurante.projectvoice_gdx.ui.UiUtil.scaledUi
import com.kurante.projectvoice_gdx.ui.textButton
import ktx.actors.onChange
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import kotlin.random.Random

class StorageScreen(private val parent: ProjectVoice) : GameScreen(parent) {
    override fun show() {
        super.show()
        stage.clear()

        val table = scene2d.table {
            setFillParent(true)

            label("This is a label") {
                this.setAlignment(Align.center)
                it.colspan(2)
            }
            label("")

            defaults().pad(8f.scaledUi(), 8f.scaledUi(), 8f.scaledUi(), 8f.scaledUi()).row()

            textButton("Button with Text").onChange {
                mainColor = Color(
                    Random.nextFloat(),
                    Random.nextFloat(),
                    Random.nextFloat(),
                    1f,
                )
            }

            imageTextButton("Button with Icon and Text", skin.getDrawable("folder_open_shadow"))

            defaults().row()

            textButton("HELL")
        }

        stage.addActor(table)
    }
}