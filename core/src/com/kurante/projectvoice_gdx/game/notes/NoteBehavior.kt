package com.kurante.projectvoice_gdx.game.notes

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.kurante.projectvoice_gdx.PlayerPreferences
import com.kurante.projectvoice_gdx.game.GameState
import com.kurante.projectvoice_gdx.game.GameplayLogic
import com.kurante.projectvoice_gdx.game.Note
import com.kurante.projectvoice_gdx.game.NoteGrade
import com.kurante.projectvoice_gdx.util.UserInterface.scaledStageX
import com.kurante.projectvoice_gdx.util.extensions.mapRange
import com.kurante.projectvoice_gdx.util.extensions.set
import kotlin.math.max

open class NoteBehavior(
    private val prefs: PlayerPreferences,
    private val atlas: TextureAtlas,
    val data: Note,
    private val state: GameState,
) {
    companion object {
        const val FADE_TIME: Int = 1000
        var isAuto = false//true
    }

    val id: Int get() = data.id
    var isCollected = false
    var shouldRender = false
    var y: Float = 0f
    private var alpha: Float = 1f
    private val speed = Note.scrollDurations[prefs.noteSpeedIndex]
    var grade: NoteGrade? = null

    private val background: TextureRegion = atlas.findRegion("click_back")
    private val foreground: TextureRegion = atlas.findRegion("click_fore")

    fun act(time: Int, screenHeight: Float, judgementLinePosition: Float, missDistance: Float) {
        val difference = data.time - time
        shouldRender = difference <= speed

        // Miss animation
        if (isCollected) {
            if (grade != NoteGrade.MISS) {
                shouldRender = false
                return
            }

            val fadeTime = NoteGrade.missThreshold - FADE_TIME
            if (fadeTime < difference) {
                y = difference.mapRange(NoteGrade.missThreshold, fadeTime, judgementLinePosition, judgementLinePosition - missDistance)
                alpha = 0.5f - difference.mapRange(NoteGrade.missThreshold, fadeTime, 0f, 0.5f)
            //alpha = 0.5f + sinceMiss * 0.5f
            } else
                shouldRender = false
            return
        }

        y = max(difference.mapRange(0, speed, judgementLinePosition, screenHeight), judgementLinePosition)
        if ((isAuto && difference <= 0) || difference < NoteGrade.missThreshold)
            judge(time)
    }

    fun render(batch: Batch, info: GameplayLogic.TrackInfo, stage: Stage) {
        if (!shouldRender) return

        val width = 85f.scaledStageX(stage)
        val drawX =  info.center - width * 0.5f
        val drawY = y - width * 0.5f

        batch.color = batch.color.set(prefs.noteClickBackground, alpha)
        batch.draw(background, drawX, drawY, width, width)
        batch.color = batch.color.set(prefs.noteClickForeground, alpha)
        batch.draw(foreground, drawX, drawY, width, width)
    }

    fun judge(time: Int) {
        val difference = data.time - time
        grade = NoteGrade.fromTime(difference) ?: return

        if (isAuto) {
            // TODO: Activate tracks behind this note
        }

        state.judge(data, grade!!, difference)
        isCollected = true
    }
}