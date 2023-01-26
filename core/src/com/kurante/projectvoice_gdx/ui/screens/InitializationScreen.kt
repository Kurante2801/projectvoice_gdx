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

class InitializationScreen(
    private val parent: ProjectVoice,
) : GameScreen(parent) {
    private val prefs = ProjectVoice.getPreferences()

    override fun show() {
        Gdx.input.inputProcessor = stage
        stage.clear()

        val tree = prefs.get<String?>("LevelTree", null)
            ?: return parent.changeScreen<StorageScreen>() // First time run

        val handle = storageHandler.directoryFromString(tree)

        val table = scene2d.table {
            setFillParent(true)
            val lbl = label("Loading levels...")

            val executor = newSingleThreadAsyncContext()
            KtxAsync.launch(executor) {
                try {
                    LevelManager.loadLevels(handle)
                    // TODO: Change to home screen (level selection)
                    lbl.setText("Loaded ${LevelManager.levels.size} levels")
                } catch (e: Exception) {
                    this@InitializationScreen.parent.changeScreen<StorageScreen>()
                }
            }
        }

        stage.addActor(table)
    }
}