package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.kurante.projectvoice_gdx.PlayerPreferences
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.ui.GameScreen
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
import com.kurante.projectvoice_gdx.ui.widgets.*
import de.tomgrill.gdxdialogs.core.GDXDialogs

class PreferencesScreen(game: ProjectVoice) : GameScreen(game) {
    private val tabMenu = TabMenu()

    override fun populate() {
        // Table only holds the return button
        val table = scene2d.table {
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
                        game.changeScreen<HomeScreen>()
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
            add(SelectionPreferenceSection("prefs_language_title", "prefs_language_subtext")
            {
                it.addChoice("English", "en")
                it.addChoice("Espa??ol", "es")
                it.setSelectedData(game.prefs.locale)

                onChange {
                    game.prefs.locale = it.selected.data as String
                }
            }).growX().pad(0f, 0f, 14f.scaledUi(), 0f).row()

            addSliderPrefs(this, "prefs_music", game.prefs.musicVolume, 0f, 1f, 0.05f, 100f, game.dialogs) {
                game.prefs.musicVolume = it
            }

            addSliderPrefs(this, "prefs_backgroundOpacity", game.prefs.backgroundOpacity, 0f, 1f, 0.05f, 100f, game.dialogs) {
                game.prefs.backgroundOpacity = it
            }


            addSliderPrefs(this, "prefs_backgroundBlur", game.prefs.backgroundBlur, 0f, 4f, 0.25f, 1f, game.dialogs) {
                game.prefs.backgroundBlur = it
                game.updateBackground()
            }

            add(BooleanPreferenceSection("prefs_safearea_title", "prefs_safearea_subtext", "common_yes", "common_no", game.prefs.safeArea) {
                onChange {
                    game.prefs.safeArea = it.value
                }
            }).growX().pad(0f, 0f, 14f.scaledUi(), 0f).row()


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

        tables.add(tabMenu)
        stage.addActor(tabMenu)
        tables.add(table)
        stage.addActor(table)
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

    private fun addSliderPrefs(
        actor: KTableWidget,
        key: String,
        value: Float,
        min: Float,
        max: Float,
        step: Float,
        displayMultiplier: Float,
        dialogs: GDXDialogs,
        onChange: (Float) -> Unit
    ) {
        actor.add(SliderPreferenceSection(
            "${key}_title", "${key}_subtext", value * displayMultiplier, min * displayMultiplier, max * displayMultiplier, step * displayMultiplier, dialogs
        ) {
            it.onChange {
                onChange(this.value / displayMultiplier)
            }
        }).growX().pad(0f, 0f, 14f.scaledUi(), 0f).row()
    }
}