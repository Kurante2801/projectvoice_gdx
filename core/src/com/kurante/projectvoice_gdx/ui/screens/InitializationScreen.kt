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

class InitializationScreen(game: ProjectVoice) : GameScreen(game) {
    override fun show() {
        super.show()

        val tree = game.prefs.storageString
            ?: return game.changeScreen<StorageScreen>(false) // First time run

        val handle = storageHandler.directoryFromString(tree)

        table = scene2d.table {
            setFillParent(true)
            label("Loading levels...")

            KtxAsync.launch(newSingleThreadAsyncContext()) {
                try {
                    LevelManager.loadLevels(handle)
                    this@InitializationScreen.game.changeScreen<HomeScreen>()
                    //this@InitializationScreen.parent.changeScreen<PreferencesScreen>()
                } catch (e: Exception) {
                    this@InitializationScreen.game.changeScreen<StorageScreen>()
                    Gdx.app.error("InitializationScreen", "Failed to load levels, moving to storage screen", e)
                }
            }
        }

        stage.addActor(table)
    }
}