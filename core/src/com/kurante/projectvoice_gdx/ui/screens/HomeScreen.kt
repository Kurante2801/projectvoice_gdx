package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.Gdx
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.level.LevelManager
import com.kurante.projectvoice_gdx.ui.GameScreen
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table

class HomeScreen(
    parent: ProjectVoice,
) : GameScreen() {

    override fun show() {
        Gdx.input.inputProcessor = stage
        stage.clear()

        val table = scene2d.table {
            setFillParent(true)
            debug = true

            //label(LevelManager.levels.joinToString { "${it.title}\n" })
            label(LevelManager.levels.random().title)
        }

        stage.addActor(table)
    }
}