package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.GdxRuntimeException
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.ProjectVoice.Companion.getPreferences
import com.kurante.projectvoice_gdx.level.LevelManager
import com.kurante.projectvoice_gdx.storage.StorageManager.storageHandler
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.PVImageTextButton
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import com.kurante.projectvoice_gdx.util.extensions.pvImageTextButton
import com.kurante.projectvoice_gdx.util.extensions.textField
import kotlinx.coroutines.launch
import ktx.actors.onChange
import ktx.app.Platform
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext
import ktx.preferences.get
import ktx.preferences.set
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table

class StorageScreen(parent: ProjectVoice) : GameScreen(parent) {
    private val prefs = getPreferences()

    override fun populate() {
        val tree = prefs.get<String?>("LevelTree", null)
            ?: return showFirstTime()

        val msg = if (LevelManager.loaded) "Loaded ${LevelManager.levels.size} levels." else "Loading..."

        table = scene2d.table {
            setFillParent(true)

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
                this.group.reverse()

                onChange {
                    this@StorageScreen.parent.changeScreen<HomeScreen>()
                }
            }

            browse.onChange {
                isDisabled = true
                next.isDisabled = true

                storageHandler.requestFolderAccess { handle ->
                    browse.isDisabled = handle != null || !LevelManager.loaded
                    next.isDisabled = handle != null || !LevelManager.loaded

                    if (handle != null)
                        tryLoadLevels(handle, message, field, browse, next)
                }
            }

            // Try to display a text instead of just an empty box
            try {
                val nameHandle = storageHandler.directoryFromString(tree)
                field.text = nameHandle.name()!!
            } catch (_: Exception) { }

            // Start loading
            if (LevelManager.loaded) return@table

            val handle = storageHandler.directoryFromString(tree)
            if (!handle.isDirectory) {
                message.setText("Failed to load levels from the given directory. Please try again")
                browse.isDisabled = false
                return@table
            }

            tryLoadLevels(handle, message, field, browse, next)
        }

        stage.addActor(table)
    }

    // First thing you see upon launching the game for the first time
    private fun showFirstTime() {
        table = scene2d.table {
            setFillParent(true)

            label("Project Voice requires access to a folder to load levels from\nIt can be an empty folder that you will later add levels to") {
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
                        if (handle != null) {
                            prefs["LevelTree"] = handle.path()
                            prefs.flush()

                            stage.clear()
                            populate()
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
        browse: PVImageTextButton,
        next: PVImageTextButton
    ) {
        var _handle = handle
        var nomediaText: String? = null

        // Make sure we're not using the root of the device!
        if (_handle.path() == "/tree/primary:" || _handle.path() == "/tree/primary:/document/primary:") {
            _handle = storageHandler.subDirectory(_handle, "Project Voice")
            if (!_handle.exists() || !_handle.isDirectory)
                throw GdxRuntimeException("Could not create nor access subfolder 'Project Voice' when granted access to the root of the device")
        }

        message.setText("Loading...")

        // Create .nomedia
        Platform.runOnAndroid {
            var nomedia = _handle.child(".nomedia")
            if (!nomedia.exists()) {
                nomedia = storageHandler.subFile(_handle, ".nomedia")
                nomediaText =
                    if (nomedia.exists()) "Created .nomedia file" else "Could not create .nomedia file"
            } else
                nomediaText = "Found .nomedia file"
        }

        if (nomediaText != null)
            message.setText("Loading. $nomediaText")

        field.text = _handle.name()

        prefs["LevelTree"] = _handle.path()
        prefs.flush()

        KtxAsync.launch(newSingleThreadAsyncContext()) {
            LevelManager.loadLevels(_handle)

            nomediaText = if (nomediaText == null)
                "Success! Loaded ${LevelManager.levels.size} levels"
            else
                "Success! Loaded ${LevelManager.levels.size} levels\n$nomediaText"

            message.setText(nomediaText)
            browse.isDisabled = false
            next.isDisabled = false
        }
    }

    private fun tryLoadLevels(
        handle: FileHandle,
        message: Label,
        field: TextField,
        browse: PVImageTextButton,
        next: PVImageTextButton
    ) {
        try {
            loadLevels(handle, message, field, browse, next)
        } catch (e: Exception) {
            browse.isDisabled = false
            next.isDisabled = !LevelManager.loaded

            message.setText("Could not load levels.\n${e.message}")
            e.printStackTrace()
        }
    }
}