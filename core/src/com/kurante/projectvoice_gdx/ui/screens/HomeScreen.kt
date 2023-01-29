package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils.ceil
import com.badlogic.gdx.utils.Align
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.level.LevelManager
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.levelCard
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import com.kurante.projectvoice_gdx.util.extensions.pvImageTextButton
import com.kurante.projectvoice_gdx.util.extensions.setMainColor
import ktx.actors.onChange
import ktx.scene2d.horizontalGroup
import ktx.scene2d.scene2d
import ktx.scene2d.scrollPane
import ktx.scene2d.table

class HomeScreen(
    val parent: ProjectVoice,
) : GameScreen() {

    override fun show() {
        Gdx.input.inputProcessor = stage
        stage.clear() // TODO: See if we can avoid this (to remember scroll positions between screens)

        table = scene2d.table {
            setFillParent(true)
            debug = true

            horizontalGroup {
                align(Align.right)
                space(28f.scaledUi())

                it.growX()
                it.pad(28f.scaledUi())

                pvImageTextButton("Options", this@table.skin.getDrawable("settings_shadow")) {
                    onChange {
                        this@HomeScreen.parent.changeScreen<StorageScreen>()
                    }
                }
            }

            defaults().row()

            scrollPane {
                it.grow()
                it.pad(0f, 28f.scaledUi(), 28f.scaledUi(), 28f.scaledUi())
                fadeScrollBars = false
                setMainColor()

                table {
                    val levels = LevelManager.levels.sortedBy { level -> level.title }

                    val columns = 3
                    val rows = ceil(levels.size / columns.toFloat())


                    grid@ for(y in 0 until rows) {
                        for(x in 0 until columns) {
                            val i = x + y * columns
                            if(i >= levels.size) break@grid

                            levelCard(levels[i], this@HomeScreen.parent.assetStorage) { cell ->
                                cell.growX()

                                val padBottom = if(y + 1 == rows) 0f else 28f.scaledUi()
                                cell.pad(0f, 0f, padBottom, 28f.scaledUi())
                            }
                        }

                        defaults().row()
                    }
                }
            }
        }

        stage.addActor(table)
    }
}