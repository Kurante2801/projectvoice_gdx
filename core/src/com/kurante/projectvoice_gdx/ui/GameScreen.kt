package com.kurante.projectvoice_gdx.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.MathUtils.lerp
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import com.kurante.projectvoice_gdx.ProjectVoice
import ktx.app.KtxScreen
import ktx.graphics.use

open class GameScreen(private val parent: ProjectVoice) : KtxScreen {
    var buffer = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, false)
    val stage = Stage(WidthViewport())
    var opacity: Float = 1f

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun resize(width: Int, height: Int) = Gdx.app.postRunnable {
        stage.viewport.update(width, height, true)
        buffer = FrameBuffer(Pixmap.Format.RGBA8888, width, height, false)
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    fun renderToBuffer(delta: Float) {
        buffer.use {
            render(delta)
        }
    }

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