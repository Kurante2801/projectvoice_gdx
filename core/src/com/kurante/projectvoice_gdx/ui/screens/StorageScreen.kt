package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.ProjectVoice.Companion.getPreferences
import com.kurante.projectvoice_gdx.level.LevelManager
import com.kurante.projectvoice_gdx.storage.StorageManager.storageHandler
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.UiUtil.scaledUi
import com.kurante.projectvoice_gdx.ui.pvImageTextButton
import com.kurante.projectvoice_gdx.ui.textField
import kotlinx.coroutines.launch
import ktx.actors.onChange
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext
import ktx.preferences.get
import ktx.preferences.set
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table

class StorageScreen(
    private val parent: ProjectVoice,
) : GameScreen(parent) {
    private val prefs = getPreferences()

    override fun show() {
        Gdx.input.inputProcessor = stage
        stage.clear()

        val tree = prefs.get<String?>("LevelTree", null)
            ?: return showFirstTime()

        val msg = if(LevelManager.loaded) "Loaded ${LevelManager.levels.size} levels." else "Loading..."

        val table = scene2d.table {
            setFillParent(true)
            //debug = true

            val message = label(msg) {
                this.setAlignment(Align.center)
                it.colspan(2)
                it.fillX()
            }

            defaults().row()
            val field = textField {
                isDisabled = true
                it.colspan(2)
                it.minWidth(416f.scaledUi())
                this.alignment = Align.center
            }

            defaults().center().row()
            val browse = pvImageTextButton("Browse", skin.getDrawable("folder_open_shadow")) {
                isDisabled = !LevelManager.loaded
                it.minWidth(200f.scaledUi())
                it.align(Align.right)
                it.pad(8f.scaledUi(), 0f, 0f, 8f.scaledUi())
            }

            val next = pvImageTextButton("Next", skin.getDrawable("forward_shadow")) {
                isDisabled = !LevelManager.loaded
                it.minWidth(200f.scaledUi())
                it.align(Align.left)
                it.pad(8f.scaledUi(), 8f.scaledUi(), 0f, 0f)
            }

            browse.onChange {
                isDisabled = true
                next.isDisabled = true

                storageHandler.requestFolderAccess { handle ->
                    browse.isDisabled = handle != null || !LevelManager.loaded
                    next.isDisabled = handle != null || !LevelManager.loaded

                    if(handle != null) {
                        prefs["LevelTree"] = handle.toString()
                        prefs.flush()
                        loadLevels(handle, message, field, browse, next)
                    }
                }
            }

            // Start loading
            if(LevelManager.loaded) return@table

            val handle = storageHandler.directoryFromString(tree)
            if(!handle.isDirectory) {
                message.setText("Failed to load levels from the given directory. Please try again")
                browse.isDisabled = false
                return@table
            }

            loadLevels(handle, message, field, browse, next)
        }

        stage.addActor(table)
    }

    // First thing you see upon launching the game for the first time
    private fun showFirstTime() {
        val table = scene2d.table {
            setFillParent(true)
            //debug = true

            val message = label("Project Voice requires access to a folder to load levels from\nIt can be an empty folder that you will later add levels to") {
                this.setAlignment(Align.center)
                it.fillX()
            }

            defaults().pad(8f.scaledUi(), 8f.scaledUi(), 8f.scaledUi(), 8f.scaledUi()).row()

            pvImageTextButton("Browse", skin.getDrawable("folder_open_shadow")) {
                it.minWidth(200f.scaledUi())
                it.uniformX()

                onChange {
                    isDisabled = true
                    storageHandler.requestFolderAccess { handle ->
                        isDisabled = false
                        if(handle != null) {
                            prefs["LevelTree"] = handle.toString()
                            prefs.flush()
                            show()
                        }
                    }
                }
            }
        }

        stage.addActor(table)
    }

    private fun loadLevels(
        handle: FileHandle,
        message: Label,
        field: TextField,
        browse: ImageTextButton,
        next: ImageTextButton
    ) {
        message.setText("Loading...")
        field.text = handle.name()

        val executor = newSingleThreadAsyncContext()
        KtxAsync.launch(executor) {
            LevelManager.loadLevels(handle)
            message.setText("Success! Loaded ${LevelManager.levels.size} levels")
            browse.isDisabled = false
            next.isDisabled = false
        }
    }
}