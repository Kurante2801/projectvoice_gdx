package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.layout.GridGroup
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.level.LevelManager
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.widgets.levelCard
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import com.kurante.projectvoice_gdx.util.extensions.pvImageTextButton
import com.kurante.projectvoice_gdx.util.extensions.setMainColor
import ktx.actors.onChange
import ktx.scene2d.*
import ktx.scene2d.Scene2DSkin.defaultSkin
import ktx.scene2d.vis.gridGroup

class HomeScreen(game: ProjectVoice) : GameScreen(game) {
    lateinit var grid: GridGroup
    override fun populate() {
        table = scene2d.table {
            setFillParent(true)

            horizontalGroup {
                align(Align.right)
                space(28f.scaledUi())

                it.growX()
                it.pad(28f.scaledUi())

                pvImageTextButton("common_options", defaultSkin.getDrawable("settings_shadow")) {
                    setLocalizedText("common_options")
                    onChange {
                        this@HomeScreen.game.changeScreen<PreferencesScreen>()
                    }
                }
            }

            defaults().row()

            scrollPane {
                it.grow()
                it.pad(0f, 28f.scaledUi(), 28f.scaledUi(), 28f.scaledUi())
                fadeScrollBars = false
                setScrollingDisabled(true, false)
                setMainColor()

                grid = gridGroup {
                    spacing = 28f.scaledUi()

                    val levels = LevelManager.levels.sortedBy { level -> level.title }
                    for (level in levels) {
                        levelCard(level, this@HomeScreen.game.assetStorage) {
                            onChange {
                                game.changeScreen<GameplayScreen>()
                                game.getScreen<GameplayScreen>().initialize(level, level.charts.last())
                            }
                        }
                    }
                }

                setGridSize()
            }
        }

        stage.addActor(table)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        setGridSize()
    }

    private fun setGridSize() {
        val w = (stage.width - game.safeLeft().scaledUi() - game.safeRight().scaledUi() - 28f.scaledUi() * 6) / 3f

        grid.apply {
            itemWidth = w
            itemHeight = w / (16f / 9f)
            invalidateHierarchy()
        }
    }
}