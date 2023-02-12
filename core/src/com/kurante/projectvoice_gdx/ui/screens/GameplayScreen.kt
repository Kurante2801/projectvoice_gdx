package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.PixmapPacker
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
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
import com.kurante.projectvoice_gdx.util.UserInterface.scaledStage
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import com.kurante.projectvoice_gdx.util.extensions.pvImageTextButton
import com.kurante.projectvoice_gdx.util.extensions.toMillis
import com.kurante.projectvoice_gdx.util.extensions.toSeconds
import kotlinx.coroutines.launch
import ktx.actors.onChange
import ktx.assets.disposeSafely
import ktx.async.KtxAsync
import ktx.graphics.use
import ktx.scene2d.*
import ktx.scene2d.Scene2DSkin.defaultSkin
import java.text.DecimalFormat
import kotlin.math.ceil

class GameplayScreen(parent: ProjectVoice) : GameScreen(parent) {
    data class TrackDrawInstruction(
        val x: Float,
        val width: Float,
        val color: Color
    )

    lateinit var level: Level
    lateinit var chart: Chart
    var conductor: Conductor? = null

    var conductorInitialized = false
    var atlasInitialized = false
    val initialized: Boolean
        get() = conductorInitialized && atlasInitialized

    private lateinit var pauseButton: PVImageTextButton
    private lateinit var statusText: Label

    val packer = PixmapPacker(2048, 2048, Pixmap.Format.RGBA8888, 2, false)
    lateinit var atlas: TextureAtlas
    val drawCalls = mutableListOf<TrackDrawInstruction>()

    lateinit var background: AtlasRegion
    lateinit var line: AtlasRegion

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
                        if (initialized && conductor!!.loaded)
                            conductor!!.paused = !conductor!!.paused
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
        if (conductor != null) {
            conductor!!.act(delta)
            if (conductor!!.loaded) {
                pauseButton.text = DecimalFormat("0.##").format(conductor!!.time.toSeconds())
                statusText.setText("BEGUN: ${conductor!!.begunPlaying} PAUSED: ${conductor!!.paused} MAX: ${conductor!!.maxTime} LENGTH: ${conductor!!.sound.length} FILE: ${conductor!!.file.name()}")
            }
        }

        if (initialized)
            gameplayRender()

        super.render(delta)
    }

    override fun hide() {
        if(conductor != null) {
            conductor.disposeSafely()
            conductor = null
        }

        if(this::atlas.isInitialized)
            atlas.disposeSafely()

        conductorInitialized = false
        atlasInitialized = false
        super.hide()
    }

    fun initialize(level: Level, section: ChartSection) {
        this.level = level
        chart = Legacy.parseChart(level, section)

        conductor = Conductor(parent.absoluteStorage, level.file.child(level.musicFilename)) {
            if (it == null)
                throw GdxRuntimeException("Conductor was not loaded!")

            it.maxTime = it.sound.length.toMillis()
            conductorInitialized = true
        }

        if (atlasInitialized) return
        KtxAsync.launch {
            packer.packToTexture = true

            packer.pack("background", parent.internalStorage.load<Pixmap>("game/track_background.png"))
            packer.pack("line", parent.internalStorage.load<Pixmap>("game/track_line.png"))

            atlas = packer.generateTextureAtlas(TextureFilter.MipMap, TextureFilter.MipMap, true)

            background = atlas.findRegion("background")
            line = atlas.findRegion("line")

            atlasInitialized = true
        }
    }

    fun gameplayRender() {
        val time = conductor!!.time

        val width = stage.width
        val height = stage.height
        val trackWidth = width * 0.115f
        val lineThick = 4f.scaledStage(stage)

        drawCalls.clear()
        for (track in chart.tracks) {
            if (time < track.spawnTime || time > track.despawnTime + track.despawnDuration) continue

            val w = track.getWidth(time, trackWidth, 12f.scaledStage(stage))
            val x = track.getPosition(time) * width - w * 0.5f
            val c = track.getColor(time)

            drawCalls.add(TrackDrawInstruction(x, w, c))
        }

        val original = stage.batch.color
        stage.batch.use {
            // Draw glows here
            // Draw backgrounds here
            for (call in drawCalls) {
                it.color = call.color
                it.draw(background, call.x, 0f, call.width, height)
            }
            // Draw white borders here
            it.color = Color.WHITE
            for (call in drawCalls) {
                it.draw(line, call.x, 0f, lineThick, height * 1.7083f)
                it.draw(line, call.x + call.width - lineThick, 0f, lineThick, height * 1.7083f)
            }
            // Draw black centers here
            it.color = Color.BLACK
            for (call in drawCalls) {
                it.draw(line, call.x + call.width * 0.5f, height * -0.2f, lineThick, height   * 1.8f)
            }
            // Draw notes here
            // Draw effects here?
        }
        stage.batch.color = original

    }
}