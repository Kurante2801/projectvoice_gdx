package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.game.Chart
import com.kurante.projectvoice_gdx.game.Legacy
import com.kurante.projectvoice_gdx.level.ChartSection
import com.kurante.projectvoice_gdx.level.Level
import com.kurante.projectvoice_gdx.ui.GameScreen
import ktx.scene2d.Scene2DSkin.defaultSkin
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table

class GameplayScreen(parent: ProjectVoice) : GameScreen(parent) {
    lateinit var level: Level
    lateinit var chart: Chart
    lateinit var hell: Label

    var initialized = false

    override fun show() {
        super.show()

        val table = scene2d.table {
            debug = true
            setFillParent(true)
            hell = label("LOADING") {
                setAlignment(Align.center)
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

    fun initialize(level: Level, section: ChartSection) {
        this.level = level
        chart = Legacy.parseChart(level, section)
        initialized = true
    }
}