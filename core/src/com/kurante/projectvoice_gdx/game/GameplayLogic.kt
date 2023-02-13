package com.kurante.projectvoice_gdx.game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils.round
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.kurante.projectvoice_gdx.util.UserInterface.scaledStage
import ktx.assets.disposeSafely
import ktx.graphics.use

class GameplayLogic(
    private val conductor: Conductor,
    chart: Chart,
    private val trackAtlas: TextureAtlas,
) : Disposable {
    private data class DrawCall(
        var center: Int = 240,
        var width: Float = 1f,
        var color: Color = Color(1f, 1f, 1f, 1f),
        var shouldDraw: Boolean = false,
    )

    private val data = mutableMapOf<Track, DrawCall>()

    // TEXTURES
    val trackBackground = trackAtlas.findRegion("background")
    val trackLine = trackAtlas.findRegion("line")
    val trackGlow = trackAtlas.findRegion("glow")

    init {
        for (track in chart.tracks)
            data[track] = DrawCall()
    }

    fun act(delta: Float) {
        conductor.act(delta)
    }

    fun render(stage: Stage, batch: SpriteBatch) {
        val time = conductor.time
        val width = stage.width
        val height = stage.height
        val trackWidth = width * 0.115f
        val borderThick: Float = 3f.scaledStage(stage)
        val centerThick: Float = 2f.scaledStage(stage)
        val glowWidth: Float = 12f.scaledStage(stage)

        for ((track, call) in data) {
            call.shouldDraw = time >= track.spawnTime && time <= track.despawnTime + track.despawnDuration

            if (call.shouldDraw) {
                call.center = round(track.getPosition(time) * width)
                call.width = track.getWidth(time, trackWidth, glowWidth)
                call.color.set(track.getColor(time))
            }
        }

        batch.use {
            it.enableBlending()

            // LEFT & RIGHT GLOWS
            for ((_, call) in data) {
                if(!call.shouldDraw) continue
                it.color = call.color
                it.draw(trackGlow, call.center - call.width * 0.5f - glowWidth, 0f, glowWidth, height)
                it.draw(trackGlow, call.center + call.width * 0.5f + glowWidth, 0f, -glowWidth, height)
            }
            // BACKGROUND
            for ((_, call) in data) {
                if (!call.shouldDraw) continue
                it.color = call.color
                it.draw(trackBackground, call.center - call.width * 0.5f, 0f, call.width, height)
            }
            // LEFT & RIGHT BORDERS
            it.color = Color.WHITE
            for ((_, call) in data) {
                if (!call.shouldDraw) continue
                it.draw(trackLine, call.center - call.width * 0.5f, height * -0.688f, borderThick, height * 1.7083f)
                it.draw(trackLine, call.center + call.width * 0.5f - borderThick, height * -0.688f, borderThick, height * 1.7083f)
            }
            // BLACK CENTER
            it.color = Color.BLACK
            for ((_, call) in data) {
                if (!call.shouldDraw) continue
                it.draw(trackLine, call.center - centerThick * 0.5f, height * -0.688f, borderThick, height * 1.7083f)
            }
            // NOTES
            // EFFECTS
        }
    }

    override fun dispose() {
        conductor.dispose()
        trackAtlas.dispose()
    }

    fun setPaused(paused: Boolean) {
        conductor.paused = paused
    }

    fun getPaused() = conductor.paused
}