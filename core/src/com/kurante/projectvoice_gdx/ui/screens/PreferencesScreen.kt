package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.pvTextButton
import com.kurante.projectvoice_gdx.util.TabMenu
import com.kurante.projectvoice_gdx.util.UserInterface
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import com.kurante.projectvoice_gdx.util.extensions.padInset
import com.kurante.projectvoice_gdx.util.extensions.pvImageTextButton
import ktx.actors.onChange
import ktx.scene2d.*
import ktx.scene2d.Scene2DSkin.defaultSkin
import java.util.Random

class PreferencesScreen(parent: ProjectVoice) : GameScreen(parent) {
    private val tabMenu = TabMenu()

    override fun populate() {
        table = scene2d.table {
            setFillParent(true)

            horizontalGroup {
                align(Align.left)
                space(28f.scaledUi())

                it.growX()
                it.pad(28f.scaledUi())

                pvImageTextButton("common_return", defaultSkin.getDrawable("back_shadow")) {
                    setLocalizedText("common_return")

                    onChange {
                        UserInterface.mainColor = Color.CYAN
                        this@PreferencesScreen.parent.changeScreen<HomeScreen>()
                    }
                }
            }

            defaults().row()

            container {
                it.grow()
                it.pad(0f, 28f.scaledUi(), 28f.scaledUi(), 28f.scaledUi())
            }
        }

        tabMenu.apply {
            setFillParent(true)

            addTab("Tab 1", scene2d.table {
                setFillParent(true)

                pvImageTextButton("Tab 1") {
                    onChange {
                        Gdx.app.log("HELL", "TAB 1 PRESSED")
                    }
                }
            })

            addTab("Tab 2", scene2d.table {
                setFillParent(true)

                pvImageTextButton("Tab 2") {
                    onChange {
                        Gdx.app.log("HELL", "TAB 2 PRESSED")
                    }
                }
            })

            addTab("Tab 3", "settings_shadow", scene2d.table {
                setFillParent(true)

                pvImageTextButton("Tab 3") {
                    onChange {
                        Gdx.app.log("HELL", "TAB 3 PRESSED")
                    }
                }
            })
        }

        stage.addActor(tabMenu)
        stage.addActor(table)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        tabMenu.padInset()
    }
}