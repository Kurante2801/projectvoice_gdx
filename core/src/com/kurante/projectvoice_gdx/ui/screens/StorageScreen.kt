package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.WidthViewport.Companion.scaledUi
import ktx.scene2d.*

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

            defaults().pad(8f.scaledUi(), 8f.scaledUi(), 8f.scaledUi(), 8f.scaledUi()).row()

            textButton("Button with Text") {
                it.prefSize(140f.scaledUi(), 48f.scaledUi())
                this.pad(0f, 8f.scaledUi(), 0f, 8f.scaledUi())
                this.color = Color.valueOf("#FF4B00")
            }

            imageTextButton("Button with Icon and Text") {
                it.prefSize(140f.scaledUi(), 48f.scaledUi())
                this.pad(0f, 8f.scaledUi(), 0f, 8f.scaledUi())
                this.style.imageUp = skin.getDrawable("folder_open_shadow")
                this.color = Color.valueOf("#FF4B00")
            }

            defaults().row()

            textButton("HELL") {
                it.prefSize(140f.scaledUi(), 48f.scaledUi())
                it.colspan(2)
                this.pad(0f, 8f.scaledUi(), 0f, 8f.scaledUi())
                this.color = Color.valueOf("#FF4B00")
            }
        }

        stage.addActor(table)
    }
}