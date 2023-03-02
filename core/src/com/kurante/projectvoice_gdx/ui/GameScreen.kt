package com.kurante.projectvoice_gdx.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.MathUtils.lerp
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.ScreenUtils
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.util.UserInterface.BACKGROUND_COLOR
import com.kurante.projectvoice_gdx.util.WidthViewport
import com.kurante.projectvoice_gdx.util.extensions.padInset
import ktx.app.KtxScreen
import ktx.graphics.use

open class GameScreen(
    val game: ProjectVoice
) : KtxScreen {
    var buffer = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, true)
    var opacity: Float = 1f
    val stage = Stage(WidthViewport())
    val tables = mutableListOf<Table>()
    var populated = false

    override fun show() {
        if(!populated) {
            populate()
            populated = true
        }
    }

    override fun resize(width: Int, height: Int) = Gdx.app.postRunnable {
        stage.viewport.update(width, height, true)

        // Minimizing on windows crashes without this
        if (width == 0 || height == 0) return@postRunnable

        for (table in tables)
            table.padInset(game.prefs.safeArea)

        buffer.dispose()
        buffer = FrameBuffer(Pixmap.Format.RGBA8888, width, height, false)
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    fun renderToBuffer(delta: Float) {
        buffer.use {
            ScreenUtils.clear(0f, 0f ,0f, 0f)
            render(delta)
        }
    }

    open fun populate() {}

    class FadeAction(
        private val startAlpha: Float,
        private val endAlpha: Float,
        private val screen: GameScreen
    ) : TemporalAction() {
        init {
            duration = 0.25f
        }

        override fun update(percent: Float) {
            screen.opacity = lerp(startAlpha, endAlpha, percent)
        }

    }
}