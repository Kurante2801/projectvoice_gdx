package com.kurante.projectvoice_gdx.game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils.round
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.kurante.projectvoice_gdx.util.UserInterface.scaledStageX
import com.kurante.projectvoice_gdx.util.UserInterface.scaledStageY
import com.kurante.projectvoice_gdx.util.extensions.mapRange
import ktx.graphics.use

class GameplayLogic(
    private val conductor: Conductor,
    chart: Chart,
    private val trackAtlas: TextureAtlas,
) : Disposable {
    private data class DrawCall(
        var center: Int = 240,
        var width: Float = 100f,
        var scaleY: Float = 1f,
        var color: Color = Color(1f, 1f, 1f, 1f),
        var shouldDraw: Boolean = false,
        var animating: Boolean = false,
    )

    companion object {
        // Track's line is larger than the screen and is centered at the judgement line
        // This is so that it looks centered when the tracks spawn and despawn with animation
        const val LINE_POS_MULTIPLIER = 0.16666666f
        const val LINE_HEIGHT_MULTIPLIER = 1.7083f
    }

    private val data = mutableMapOf<Track, DrawCall>()

    // TEXTURES
    val trackBackground = trackAtlas.findRegion("background")
    val trackLine = trackAtlas.findRegion("line")
    val trackGlow = trackAtlas.findRegion("glow")
    val judgementLine = trackAtlas.findRegion("white")

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
        val borderThick: Float = 3f.scaledStageX(stage)
        val centerThick: Float = 2f.scaledStageX(stage)
        val glowWidth: Float = 12f.scaledStageX(stage)
        val judgementThick: Float = 2f.scaledStageY(stage)

        for ((track, call) in data) {
            call.shouldDraw = time >= track.spawnTime && time <= track.despawnTime + track.despawnDuration

            if (!call.shouldDraw) continue
            call.animating = false
            var scaleX = 1f
            call.scaleY = 1f

            val sinceDespawn = time - track.despawnTime
            if (sinceDespawn >= 0) {
                val t = (sinceDespawn.toFloat() / track.despawnDuration).coerceIn(0f, 1f)
                scaleX = 1f - Interpolation.pow3Out.apply(t)
                call.scaleY = 1f - Interpolation.linear.apply(t)
                call.animating = true
            }

            if (!call.animating && track.spawnDuration > 0) {
                val sinceSpawn = time - track.spawnTime
                if (sinceSpawn <= track.spawnDuration) {
                    val t = (sinceSpawn.toFloat() / track.spawnDuration).coerceIn(0f, 1f)
                    scaleX = Interpolation.elasticOut.apply(t)
                    call.scaleY = Interpolation.exp10.apply(t)
                    call.animating = true
                }
            }

            call.center = round(track.getPosition(time) * width)
            call.width = track.getWidth(time, trackWidth, glowWidth) * scaleX
            call.color.set(track.getColor(time))
        }

        batch.use {
            it.enableBlending()

            // LEFT & RIGHT GLOWS
            forEachDrawable(data) { call ->
                it.color = call.color
                val half = call.width * 0.5f

                it.draw(trackGlow, call.center - half - glowWidth, height * call.scaleY.mapRange(0.1666f, 0f), glowWidth, height * call.scaleY)
                it.draw(trackGlow, call.center + half + glowWidth, height * call.scaleY.mapRange(0.1666f, 0f), -glowWidth, height * call.scaleY)
            }
            // BACKGROUND
            forEachDrawable(data) { call ->
                it.color = call.color
                it.draw(trackBackground, call.center - call.width * 0.5f, height * call.scaleY.mapRange(0.1666f, 0f), call.width, height * call.scaleY)
            }
            // LEFT & RIGHT BORDERS
            it.color = Color.WHITE
            forEachDrawable(data) { call ->
                val half = call.width * 0.5f
                val tall = (height * LINE_HEIGHT_MULTIPLIER) * call.scaleY
                val y = (height * LINE_POS_MULTIPLIER) - tall * 0.5f
                it.draw(trackLine, call.center - half, y, borderThick, tall)
                it.draw(trackLine, call.center + half - borderThick, y, borderThick, tall)
            }
            // BLACK CENTER
            forEachDrawable(data) { call ->
                it.color = it.color.set(0f, 0f, 0f, call.scaleY)
                val tall = (height * LINE_HEIGHT_MULTIPLIER) * call.scaleY
                val y = (height * LINE_POS_MULTIPLIER) - tall * 0.5f
                it.draw(trackLine, call.center - borderThick * 0.5f, y, borderThick, tall)
            }
            // JUDGEMENT LINE
            it.color = Color.WHITE
            it.draw(judgementLine, 0f, height * LINE_POS_MULTIPLIER - judgementThick * 0.5f, width, judgementThick)
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

    private fun forEachDrawable(data: MutableMap<Track, DrawCall>, action: (DrawCall) -> Unit) {
        for (call in data.values) {
            if (call.shouldDraw)
                action(call)
        }
    }
}