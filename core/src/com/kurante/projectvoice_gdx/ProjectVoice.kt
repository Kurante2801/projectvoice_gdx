package com.kurante.projectvoice_gdx

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ScreenUtils
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.screens.StorageScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync
import ktx.graphics.use
import ktx.scene2d.Scene2DSkin

class ProjectVoice : KtxGame<KtxScreen>() {
    private lateinit var batch: SpriteBatch
    lateinit var skin: Skin

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        batch = SpriteBatch()

        skin = Skin(Gdx.files.internal("skin/skin.json"))
        Scene2DSkin.defaultSkin = skin

        KtxAsync.initiate()

        addScreen(StorageScreen(this))
        setScreen<StorageScreen>()
    }

    override fun render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f)

        val screen = currentScreen as? GameScreen
        // Used when rendering fade in/out
        if(screen != null && screen.opacity < 1f) {
            screen.renderToBuffer(Gdx.graphics.deltaTime)

            batch.use {
                // Uncomment below if rendering anything else
                // it.color = Color.WHITE

                it.color = screen.bufferColor
                it.draw(
                    screen.buffer.colorBufferTexture,
                    0f, 0f,
                    screen.buffer.width.toFloat(),
                    screen.buffer.height.toFloat(),
                    0f, 0f, 1f, 1f
                )
            }
        } else
            currentScreen.render(Gdx.graphics.deltaTime)
    }
}