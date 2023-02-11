package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.GdxRuntimeException
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.game.Chart
import com.kurante.projectvoice_gdx.game.Conductor
import com.kurante.projectvoice_gdx.game.Legacy
import com.kurante.projectvoice_gdx.level.ChartSection
import com.kurante.projectvoice_gdx.level.Level
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.widgets.PVImageTextButton
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import com.kurante.projectvoice_gdx.util.extensions.mapRange
import com.kurante.projectvoice_gdx.util.extensions.pvImageTextButton
import com.kurante.projectvoice_gdx.util.extensions.toMillis
import com.kurante.projectvoice_gdx.util.extensions.toSeconds
import ktx.actors.onChange
import ktx.assets.disposeSafely
import ktx.graphics.use
import ktx.scene2d.*
import ktx.scene2d.Scene2DSkin.defaultSkin
import java.text.DecimalFormat

class GameplayScreen(parent: ProjectVoice) : GameScreen(parent) {
    lateinit var level: Level
    lateinit var chart: Chart
    lateinit var conductor: Conductor
    var initialized = false

    private lateinit var pauseButton: PVImageTextButton
    private lateinit var statusText: Label

    val white = defaultSkin.getDrawable("white")

    override fun populate() {
        table = scene2d.table {
            setFillParent(true)

            horizontalGroup {
                align(Align.left)
                space(28f.scaledUi())
                it.growX()
                it.pad(28f.scaledUi())

                pauseButton = pvImageTextButton("LOADING") {
                    onChange {
                        if(initialized && conductor.loaded)
                            conductor.paused = !conductor.paused
                    }
                }

                statusText = label("")
            }
            defaults().row()
            container {
                it.grow()
                it.pad(0f, 28f.scaledUi(), 28f.scaledUi(), 28f.scaledUi())
            }
        }

        stage.addActor(table)
    }

    override fun render(delta: Float) {
        if (this::conductor.isInitialized) {
            conductor.act(delta)
            if (conductor.loaded) {
                pauseButton.text = DecimalFormat("0.##").format(conductor.time.toSeconds())
                statusText.setText("BEGUN: ${conductor.begunPlaying} PAUSED: ${conductor.paused} MAX: ${conductor.maxTime} LENGTH: ${conductor.sound.length} FILE: ${conductor.file.name()}")
            }
        }

        if (initialized)
            gameplayRender()

        super.render(delta)
    }

    fun initialize(level: Level, section: ChartSection) {
        this.level = level
        chart = Legacy.parseChart(level, section)

        conductor = Conductor(parent.absoluteStorage, level.file.child(level.musicFilename)) {
            if (it == null)
                throw GdxRuntimeException("Conductor was not loaded!")

            Gdx.app.log("HELL", "MAX TIME: ${conductor.sound.length}")
            conductor.maxTime = conductor.sound.length.toMillis()
            initialized = true
        }
            conductor.minTime = 10f.toMillis()
    }

    fun gameplayRender() {
        val time = conductor.time
        val e = 0.5f.mapRange(0f, 1f, 0.09375f, 0.90625f)

        val width = stage.width
        val height = stage.height
        val heightHalf = height * 0.5f

        stage.batch.begin()
        for (track in chart.tracks) {
            if (time < track.spawnTime || time > track.despawnTime + track.despawnDuration) continue

            val w = 0.10546875f * width

            val moveTransition = track.getMoveTransition(time)
            val x = moveTransition.easing.easeFunction(
                time.mapRange(moveTransition.startTime, moveTransition.endTime, 0, 1),
                moveTransition.startValue, moveTransition.endValue
            ) * width - w * 0.5f

            white.draw(stage.batch, x, 0f, w, height)
        }
        stage.batch.end()
    }
}