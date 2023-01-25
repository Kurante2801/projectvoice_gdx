package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.utils.Align
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.storage.StorageManager
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.UiUtil.scaledUi
import com.kurante.projectvoice_gdx.ui.imageTextButton
import ktx.actors.onChange
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table

class StorageScreen(parent: ProjectVoice) : GameScreen(parent) {
    override fun show() {
        super.show()
        stage.clear()

        val table = scene2d.table {
            setFillParent(true)

            label("Please select the folder your levels are stored at") {
                this.setAlignment(Align.center)
                it.colspan(2)
            }

            defaults().pad(8f.scaledUi(), 8f.scaledUi(), 8f.scaledUi(), 8f.scaledUi()).row()

            imageTextButton("Browse", skin.getDrawable("folder_open_shadow")).onChange {
                this.isDisabled = true
                StorageManager.handler.requestFolderAccess {
                    this.isDisabled = false
                    if(it != null)
                        this.text = "Subfiles: ${it.getFiles().size}"
                }
            }
        }

        stage.addActor(table)
    }
}