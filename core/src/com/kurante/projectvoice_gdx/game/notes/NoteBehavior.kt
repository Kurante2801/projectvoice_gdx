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
import com.kurante.projectvoice_gdx.util.extensions.draw
import com.kurante.projectvoice_gdx.util.extensions.mapRange

open class NoteBehavior(
    val prefs: PlayerPreferences,
    val atlas: TextureAtlas,
    val data: Note,
    val state: GameState,
) {
    companion object {
        const val FADE_TIME: Int = 1000
        var isAuto = true
    }

    val id: Int get() = data.id
    var isCollected = false
    var missAnimationEnded = false
    var shouldRender = false
    var y: Float = 0f
    var alpha: Float = 1f
    val speed = Note.scrollDurations[prefs.noteSpeedIndex]
    var grade: NoteGrade? = null
    val background: TextureRegion = atlas.findRegion("click_back")
    val foreground: TextureRegion = atlas.findRegion("click_fore")

    fun act(time: Int, screenHeight: Float, judgementLinePosition: Float) {
        val difference = data.time - time
        shouldRender = difference <= speed

        // Miss animation
        if (isCollected) {
            if (difference > -FADE_TIME) {
                val sinceMiss = (difference - NoteGrade.missThreshold) / FADE_TIME
                y = screenHeight - sinceMiss * screenHeight * 0.025f
                alpha = 0.5f + sinceMiss * 0.5f
            } else
                missAnimationEnded = true
            return
        }

        y = difference.mapRange(0, speed, judgementLinePosition, screenHeight)
        if ((isAuto && difference <= 0) || difference < NoteGrade.missThreshold)
            judge(time)
    }

    fun render(batch: Batch, info: GameplayLogic.TrackInfo, stage: Stage) {
        if (!shouldRender || missAnimationEnded) return

        val width = 85f.scaledStageX(stage)
        val drawX =  info.center - width * 0.5f
        val drawY = y - width * 0.5f
        batch.draw(prefs.noteClickBackground, background, drawX, drawY, width, width)
        batch.draw(prefs.noteClickForeground, foreground, drawX, drawY, width, width)
    }

    fun judge(time: Int) {
        val difference = data.time - time
        grade = NoteGrade.fromTime(difference) ?: return
        missAnimationEnded = grade != NoteGrade.MISS
        
        if (isAuto) {
            // TODO: Activate tracks behind this note
        }

        state.judge(data, grade!!, difference)
    }
}