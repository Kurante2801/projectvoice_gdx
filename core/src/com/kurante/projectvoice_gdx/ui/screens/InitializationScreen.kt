package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.Gdx
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.level.LevelManager
import com.kurante.projectvoice_gdx.storage.StorageManager.storageHandler
import com.kurante.projectvoice_gdx.ui.GameScreen
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext
import ktx.preferences.get
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table

class InitializationScreen(parent: ProjectVoice) : GameScreen(parent) {
    private val prefs = ProjectVoice.getPreferences()

    override fun show() {
        Gdx.input.inputProcessor = stage
        stage.clear()

        val tree = prefs.get<String?>("LevelTree", null)
            ?: return parent.changeScreen<StorageScreen>() // First time run

        val handle = storageHandler.directoryFromString(tree)

        table = scene2d.table {
            setFillParent(true)
            label("Loading levels...")

            try {
                KtxAsync.launch(newSingleThreadAsyncContext()) {
                    LevelManager.loadLevels(handle)
                    this@InitializationScreen.parent.changeScreen<HomeScreen>()
                }
            } catch (e: Exception) {
                this@InitializationScreen.parent.changeScreen<StorageScreen>()
                Gdx.app.error("InitializationScreen", "Failed to load levels, moving to storage screen", e)
            }
        }

        stage.addActor(table)
    }


}