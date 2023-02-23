package com.kurante.projectvoice_gdx.game.notes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.kurante.projectvoice_gdx.PlayerPreferences
import com.kurante.projectvoice_gdx.game.GameState
import com.kurante.projectvoice_gdx.game.GameplayLogic
import com.kurante.projectvoice_gdx.game.Note
import com.kurante.projectvoice_gdx.game.NoteGrade
import com.kurante.projectvoice_gdx.util.UserInterface.scaledStageX
import com.kurante.projectvoice_gdx.util.extensions.mapRange
import com.kurante.projectvoice_gdx.util.extensions.set
import kotlin.math.max

class HoldNoteBehavior(
    private val prefs: PlayerPreferences,
    atlas: TextureAtlas,
    data: Note,
    private val state: GameState,
    ninePatch: NinePatch,
) : NoteBehavior(prefs, atlas, data, state) {
    companion object {
        const val RELEASE_THRESHOLD = 360
        var isAuto = true
    }

    val fingers = mutableListOf<Int>()

    private val region: TextureRegion = atlas.findRegion("hold_back")
    private val backgroundPatch = NinePatch(region, ninePatch.leftWidth.toInt(), ninePatch.rightWidth.toInt(), ninePatch.topHeight.toInt(), ninePatch.bottomHeight.toInt())

    var endY: Float = 0f
    private var initialDifference = 0 // Difference when hold note was clicked
    var isBeingHeld = false
    private var initialGrade: NoteGrade? = null

    override fun act(time: Int, screenHeight: Float, judgementLinePosition: Float, missDistance: Float) {
        var difference = data.time - time
        shouldRender = difference <= speed
        if (!shouldRender) return

        // Miss animation
        if (isCollected) {
            if (grade != NoteGrade.MISS) {
                shouldRender = false
                return
            }

            difference -= initialDifference
            y = difference.mapRange(0, speed, judgementLinePosition, screenHeight)
            endY = (data.time + data.data - time).mapRange(0, speed, judgementLinePosition, screenHeight)

            shouldRender = endY < screenHeight
            alpha = 0.5f
            return
        }

        y = max(difference.mapRange(0, speed, judgementLinePosition, screenHeight), judgementLinePosition)
        endY = max((data.time + data.data - time).mapRange(0, speed, judgementLinePosition, screenHeight), judgementLinePosition)

        if (isBeingHeld) {
            val hasFingers = isAuto || fingers.isNotEmpty()
            difference = data.time + data.data - time
            // Collect
            if (!hasFingers || difference <= 0)
                judge(time, difference)
        } else {
            if (isAuto) {
                if (difference <= 0)
                    startHold(time)
            } else if (difference < NoteGrade.missThreshold)
                judge(time)
        }
    }

    override fun render(batch: Batch, info: GameplayLogic.TrackInfo, stage: Stage) {
        if (!shouldRender) return

        val width = 85f.scaledStageX(stage)
        val drawX = info.center - width * 0.5f
        val drawY = y - width * 0.5f
        val drawYend = endY - width * 0.5f

        backgroundPatch.middleWidth = width
        backgroundPatch.topHeight = width * 0.5f
        backgroundPatch.bottomHeight = width * 0.5f

        batch.color = batch.color.set(prefs.noteHoldBackground, alpha)
        backgroundPatch.draw(batch, drawX, drawY, width, width + drawYend - drawY)

        batch.color = batch.color.set(prefs.noteHoldTopForeground, alpha)
        batch.draw(foreground, drawX, drawYend, width, width)
        batch.color = batch.color.set(prefs.noteHoldBottomForeground, alpha)
        batch.draw(foreground, drawX, drawY, width, width)
    }

    override fun judge(time: Int) {
        // This makes the hold note shorten properly when releasing too early
        initialDifference = data.time - time
        super.judge(time)
    }

    fun judge(time: Int, difference: Int) {
        if (initialGrade == null) {
            Gdx.app.error("HoldNoteBehavior", "Note was judged when grade was null! ID: ${data.id}")
            return
        }

        // Note was released too early, count as a miss
        if (difference > RELEASE_THRESHOLD)
            initialGrade = NoteGrade.MISS

        grade = initialGrade
        initialDifference = data.time - time
        state.judge(data, initialGrade!!, difference)
        isCollected = true
    }

    fun startHold(time: Int) {
        initialGrade = NoteGrade.fromDifference(data.time - time)
        if (initialGrade == null) return

        if (initialGrade == NoteGrade.MISS)
            judge(initialDifference)
        else {
            isBeingHeld = true
            initialDifference = data.time - time
        }
    }
}