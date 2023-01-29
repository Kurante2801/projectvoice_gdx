package com.kurante.projectvoice_gdx.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils.ceil
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kurante.projectvoice_gdx.level.Level
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import com.kurante.projectvoice_gdx.util.extensions.crop
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin.defaultSkin
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class LevelCard(
    private val level: Level,
    private val assetStore: AssetStorage?,
) : Table() {
    private val white: TextureRegion = defaultSkin.getRegion("white")
    private val backgroundColor: Color = Color.valueOf("#323232")

    private var background: Texture? = null
    private var requestedBackground = false

    init {
        debug = false
    }

    override fun getPrefHeight(): Float {
        return 214f.scaledUi()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if(!requestedBackground)
            requestBackground()

        super.draw(batch, parentAlpha)

        batch.color = backgroundColor
        batch.draw(white, x, y, width, height)

        if(background != null) {
            batch.color = Color.WHITE
            batch.draw(background, x, y, width, height)
        }
    }

    private fun requestBackground() {
        requestedBackground = true
        if(assetStore == null || level.backgroundFilename == null) return

        // TODO: AssetDescriptor? to not reload and cause stutters after changing screens?
        KtxAsync.launch {
            val handle = level.file.child(level.backgroundFilename)
            if(!handle.exists()) return@launch

            val pixmap = assetStore.load<Pixmap>(path = handle.toString())
                .crop(ceil(width), ceil(height), level.backgroundAspectRatio)

            Gdx.app.postRunnable {
                background = Texture(pixmap)
            }
        }
    }
}

@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.levelCard(
    level: Level,
    assetStore: AssetStorage?,
    init: (@Scene2dDsl LevelCard).(S) -> Unit = {}
): LevelCard {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(LevelCard(level, assetStore), init)
}