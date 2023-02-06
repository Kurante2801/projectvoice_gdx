package com.kurante.projectvoice_gdx.ui.screens

import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.util.extensions.pvImageTextButton
import ktx.actors.onChange
import ktx.scene2d.scene2d
import ktx.scene2d.table

class PreferencesScreen(parent: ProjectVoice) : GameScreen(parent) {
    override fun populate() {
        table = scene2d.table {
            setFillParent(true)

            pvImageTextButton("RETURN") {
                onChange {
                    if (!this@PreferencesScreen.parent.tryPreviousScreen())
                        this@PreferencesScreen.parent.changeScreen<HomeScreen>()
                }
            }
        }

        stage.addActor(table)
    }
}