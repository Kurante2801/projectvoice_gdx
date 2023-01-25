package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Align
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.ProjectVoice.Companion.getPreferences
import com.kurante.projectvoice_gdx.storage.StorageManager.storageHandler
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.UiUtil.scaledUi
import com.kurante.projectvoice_gdx.ui.pvImageTextButton
import ktx.actors.onChange
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table

class StorageScreen(private val parent: ProjectVoice) : GameScreen(parent) {
    private val prefs = getPreferences()

    override fun show() {
        Gdx.input.inputProcessor = stage
        stage.clear()

        showFirstTime()
    }

    private fun showFirstTime() {
        val table = scene2d.table {
            setFillParent(true)
            debug = true

            val message = label("Project Voice requires access to a folder to load levels from\nIt can be an empty folder that you will later add levels to") {
                this.setAlignment(Align.center)
                it.fillX()
            }

            defaults().pad(8f.scaledUi(), 8f.scaledUi(), 8f.scaledUi(), 8f.scaledUi()).row()

            pvImageTextButton("Browse", skin.getDrawable("folder_open_shadow")) {
                it.uniformX()

                onChange {
                    isDisabled = true
                    storageHandler.requestFolderAccess { handle ->
                        isDisabled = false
                        if(handle == null)
                            message.setText("Could not access the given directory, please try again!")
                        else {
                            /*prefs["LevelsLocation"] = storageHandler.toString()
                            prefs.flush()
                            show()*/
                            text = "${handle.list().size}"
                        }
                    }
                }
            }
        }

        stage.addActor(table)
    }
}