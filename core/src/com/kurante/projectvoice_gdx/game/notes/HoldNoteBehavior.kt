package com.kurante.projectvoice_gdx.game.notes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.kurante.projectvoice_gdx.PlayerPreferences
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.game.*
import com.kurante.projectvoice_gdx.util.UserInterface.scaledStageX
import com.kurante.projectvoice_gdx.util.extensions.draw
import com.kurante.projectvoice_gdx.util.extensions.mapRange
import com.kurante.projectvoice_gdx.util.extensions.set
import kotlin.math.max

class HoldNoteBehavior(
    private val prefs: PlayerPreferences,
    atlas: TextureAtlas,
    data: Note,
    private val state: GameState,
    ninePatch: NinePatch,
    private val modifiers: HashSet<Modifier>,
    private val logic: GameplayLogic,
    private val track: Track
) : NoteBehavior(prefs, atlas, data, state, modifiers, logic) {
    companion object {
        const val RELEASE_THRESHOLD = 360
    }

    private data class Tick(
        val time: Int,
        val posX: Float
    )

    private val ticks: Array<Tick>

    init {
        val startX = track.getPosition(data.time)
        var moves = false

        val positions = mutableListOf<Tick>()
        for (i in 100..data.data step 100) {
            val x = track.getPosition(data.time + i)
            if (x != startX)
                moves = true
            positions.add(Tick(i, x))
        }

        ticks = if (moves)
            positions.toTypedArray()
        else {
            positions.clear()
            arrayOf()
        }
    }

    val fingers = mutableListOf<Int>()

    private val region: TextureRegion = atlas.findRegion("hold_back")
    private val backgroundPatch = NinePatch(region, ninePatch.leftWidth.toInt(), ninePatch.rightWidth.toInt(), ninePatch.topHeight.toInt(), ninePatch.bottomHeight.toInt())

    var endY: Float = 0f
    private var initialDifference = 0 // Difference when hold note was clicked
    var isBeingHeld = false
    private var initialGrade: NoteGrade? = null

    override val isAuto: Boolean
        get() = modifiers.contains(Modifier.AUTO) || modifiers.contains(Modifier.AUTO_HOLD)
    override val backColor: Color
        get() = prefs.noteHoldBackground
    override val foreColor: Color
        get() = prefs.noteHoldBottomForeground
    private val topColor: Color
        get() = prefs.noteHoldTopForeground
    private val tickBackColor: Color
        get() = prefs.noteTickBackground
    private val tickForeColor: Color
        get() = prefs.noteTickForeground

    private val tickBack = atlas.findRegion("tick_back")
    private val tickFore = atlas.findRegion("tick_fore")

    override fun act(time: Int, judgementLinePosition: Float, missDistance: Float, x: Int) {
        var difference = data.time - time
        shouldRender = difference <= speed
        if (!shouldRender) return

        val height = ProjectVoice.stageHeight
        // Miss animation
        if (isCollected) {
            if (grade != NoteGrade.MISS) {
                shouldRender = false
                return
            }

            difference -= initialDifference
            y = difference.mapRange(0, speed, judgementLinePosition, height)
            endY = (data.time + data.data - time).mapRange(0, speed, judgementLinePosition, height)

            shouldRender = endY < height
            alpha = 0.5f
            return
        }

        y = max(difference.mapRange(0, speed, judgementLinePosition, height), judgementLinePosition)
        endY = max((data.time + data.data - time).mapRange(0, speed, judgementLinePosition, height), judgementLinePosition)

        if (isBeingHeld) {
            val hasFingers = isAuto || fingers.isNotEmpty()
            difference = data.time + data.data - time
            // Collect
            if (!hasFingers || difference <= 0)
                judgeHold(time, difference)
            else
                logic.simulateTrackInput(time, x)
        } else {
            if (isAuto) {
                if (difference <= 0)
                    startHold(time, x)
            } else if (difference < NoteGrade.missThreshold)
                judge(time, x)
        }
    }

    override fun render(batch: Batch, info: GameplayLogic.TrackInfo) {
        if (!shouldRender) return

        val width = 85f.scaledStageX()
        val drawX = info.center - width * 0.5f
        val drawY = y - width * 0.5f
        val drawYend = endY - width * 0.5f

        backgroundPatch.middleWidth = width
        backgroundPatch.topHeight = width * 0.5f
        backgroundPatch.bottomHeight = width * 0.5f

        batch.color = batch.color.set(backColor, alpha)
        backgroundPatch.draw(batch, drawX, drawY, width, width + drawYend - drawY)

        batch.color = batch.color.set(topColor, alpha)
        batch.draw(foreground, drawX, drawYend, width, width)
        batch.color = batch.color.set(foreColor, alpha)
        batch.draw(foreground, drawX, drawY, width, width)
    }

    override fun judge(time: Int, x: Int) {
        // This makes the hold note shorten properly when releasing too early
        initialDifference = data.time - time
        super.judge(time, x)
    }

    fun judgeHold(time: Int, difference: Int) {
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

    fun startHold(time: Int, x: Int) {
        initialGrade = NoteGrade.fromDifference(data.time - time)
        if (initialGrade == null) return

        if (initialGrade == NoteGrade.MISS)
            judge(initialDifference, x)
        else {
            isBeingHeld = true
            initialDifference = data.time - time
        }
    }

    fun renderTicks(batch: Batch, time: Int, judgementLinePosition: Float) {
        val width = 85f.scaledStageX()
        val half = width * 0.5f
        for (tick in ticks) {
            val difference = data.time + tick.time - time
            if (difference < 0) continue
            val x = tick.posX * ProjectVoice.stageWidth - half
            val y = difference.mapRange(0, speed, judgementLinePosition, ProjectVoice.stageHeight) - half

            batch.draw(tickBackColor, tickBack, x, y, width, width)
            batch.draw(tickForeColor, tickFore, x, y, width, width)
        }
    }
}