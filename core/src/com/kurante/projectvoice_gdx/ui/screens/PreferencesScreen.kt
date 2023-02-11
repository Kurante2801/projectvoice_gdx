package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.spinner.FloatSpinnerModel
import com.kurante.projectvoice_gdx.PlayerPreferences
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.widgets.PVSlider
import com.kurante.projectvoice_gdx.ui.widgets.PreferenceSection
import com.kurante.projectvoice_gdx.ui.widgets.SelectionPreferenceSection
import com.kurante.projectvoice_gdx.util.TabMenu
import com.kurante.projectvoice_gdx.util.UserInterface
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import com.kurante.projectvoice_gdx.util.extensions.padInset
import com.kurante.projectvoice_gdx.util.extensions.pvImageTextButton
import com.kurante.projectvoice_gdx.util.extensions.setMainColor
import ktx.actors.onChange
import ktx.scene2d.*
import ktx.scene2d.Scene2DSkin.defaultSkin
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class PreferencesScreen(parent: ProjectVoice) : GameScreen(parent) {
    private val tabMenu = TabMenu()

    override fun populate() {
        // Table only holds the return button
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

        tabMenu.setFillParent(true)

        // Preferences begin here
        addTabLocalized("prefs_tab_general") {
            val language = SelectionPreferenceSection("prefs_language_title", "prefs_language_subtext")
            {
                it.addChoice("English", "en")
                it.addChoice("Español", "es")

                onChange {
                    PlayerPreferences.locale = it.selected.data as String
                }
            }
            add(language).growX().row()

            val music = PreferenceSection().apply {
                initialize("prefs_tab_general", "prefs_language_title") { container, cell ->
                    container.addActor(PVSlider(50f, 0f, 100f, 25f))
                }
            }

            add(music).growX().row()

            defaults().row()
            container {
                it.grow()
            }
        }

        addTabLocalized("prefs_tab_notes") {
            pvImageTextButton("Tab 2")
        }

        addTabLocalized("prefs_tab_others") {
            pvImageTextButton("Tab 3")
        }

        stage.addActor(tabMenu)
        stage.addActor(table)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        tabMenu.padInset()
    }

    @OptIn(ExperimentalContracts::class)
    private fun addTabLocalized(
        key: String,
        init: (@Scene2dDsl KTableWidget).(Actor) -> Unit = {}
    ) {
        contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }

        tabMenu.addTabLocalized(key, scene2d.table {
            setFillParent(true)
            scrollPane {
                fadeScrollBars = false
                it.grow()
                setMainColor()

                this.table {
                    this.pad(0f, 0f, 0f, 28f.scaledUi())
                    init(this, it)
                }
            }
        })
    }
}