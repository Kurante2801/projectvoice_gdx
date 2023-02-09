package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.game.Chart
import com.kurante.projectvoice_gdx.game.Conductor
import com.kurante.projectvoice_gdx.game.Legacy
import com.kurante.projectvoice_gdx.level.ChartSection
import com.kurante.projectvoice_gdx.level.Level
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.widgets.PVImageTextButton
import com.kurante.projectvoice_gdx.util.extensions.pvImageTextButton
import ktx.actors.onChange
import ktx.assets.disposeSafely
import ktx.scene2d.Scene2DSkin.defaultSkin
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table

class GameplayScreen(parent: ProjectVoice) : GameScreen(parent) {
    lateinit var level: Level
    lateinit var chart: Chart
    lateinit var hell: Label
    lateinit var butt: PVImageTextButton
    lateinit var conductor: Conductor

    var initialized = false

    override fun show() {
        super.show()
        stage.clear()

        val table = scene2d.table {
            debug = true
            setFillParent(true)
            hell = label("LOADING") {
                setAlignment(Align.center)
            }
            defaults().row()
            butt = pvImageTextButton("Play") {
                onChange {
                    if(!this@GameplayScreen::conductor.isInitialized || !conductor.loaded)
                        return@onChange

                    if(conductor.sound.isPlaying)
                        conductor.sound.pause()
                    else
                        conductor.sound.play()
                }
            }

            pvImageTextButton("Exit!") {
                onChange {
                    initialized = false
                    conductor.disposeSafely()
                    this@GameplayScreen.parent.tryPreviousScreen()
                }
            }
        }

        stage.addActor(table)

        if(initialized) {
            hell.setText("${level.title} by ${level.artist}\nTracks: ${chart.tracks.size}")
            hell.style = Label.LabelStyle().apply {
                font = defaultSkin.getFont("bold")
            }
        }
    }

    override fun render(delta: Float) {
        super.render(delta)
        if(this::conductor.isInitialized) {
            conductor.think(delta)
            if(conductor.loaded)
                butt.text = "${conductor.sound.cursorPosition}"
        }
    }

    override fun dispose() {
        if(this::conductor.isInitialized)
            conductor.disposeSafely()
        super.dispose()
    }

    fun initialize(level: Level, section: ChartSection) {
        this.level = level
        chart = Legacy.parseChart(level, section)
        conductor = Conductor(
            parent.absoluteStorage,
            level.file.child(level.musicFilename)
        )

        initialized = true
    }
}