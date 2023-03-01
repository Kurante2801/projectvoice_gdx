package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.level.LevelManager
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.widgets.fixedColumnGroup
import com.kurante.projectvoice_gdx.ui.widgets.levelCard
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import com.kurante.projectvoice_gdx.util.extensions.aspectRatio
import com.kurante.projectvoice_gdx.util.extensions.pvImageTextButton
import com.kurante.projectvoice_gdx.util.extensions.setMainColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ktx.actors.onChange
import ktx.async.KtxAsync
import ktx.scene2d.Scene2DSkin.defaultSkin
import ktx.scene2d.horizontalGroup
import ktx.scene2d.scene2d
import ktx.scene2d.scrollPane
import ktx.scene2d.table

class HomeScreen(game: ProjectVoice) : GameScreen(game) {
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
                        game.changeScreen<PreferencesScreen>()
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

                fixedColumnGroup(3) {
                    spacing = 28f.scaledUi()

                    val levels = LevelManager.levels.sortedBy { level -> level.title }
                    for (level in levels) {
                        levelCard(level, game.assetStorage) {
                            onChange {
                                KtxAsync.launch {
                                    val texture = (image.drawable as TextureRegionDrawable).region.texture
                                    game.setBackground(texture,
                                        level.backgroundAspectRatio ?: texture.aspectRatio, false
                                    )
                                    game.changeScreen<GameplayScreen>()
                                    delay(250)
                                    game.getScreen<GameplayScreen>().initialize(level, level.charts.last())
                                }
                            }
                        }
                    }
                }
            }
        }

        stage.addActor(table)
    }
}