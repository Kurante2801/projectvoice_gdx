@file:Suppress("MemberVisibilityCanBePrivate")

package com.kurante.projectvoice_gdx.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.kurante.projectvoice_gdx.level.Level
import com.kurante.projectvoice_gdx.util.UserInterface
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import com.kurante.projectvoice_gdx.util.extensions.crop
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.scene2d.*
import ktx.scene2d.Scene2DSkin.defaultSkin
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class LevelCard(
    private val level: Level,
    private val assetStore: AssetStorage?,
) : Table() {
    @Suppress("JoinDeclarationAndAssignment")
    val background: Image
    val image: Image
    val table: Table

    init {
        background = Image(defaultSkin.getRegion("white")).apply {
            setFillParent(true)
            color = UserInterface.FOREGROUND1_COLOR
        }

        image = Image(defaultSkin.getRegion("white")).apply {
            setFillParent(true)
            color = UserInterface.FOREGROUND1_COLOR

            if (assetStore == null || level.backgroundFilename == null) return@apply

            KtxAsync.launch {
                val handle = level.file.child(level.backgroundFilename)
                if (!handle.exists()) return@launch

                val pixmap = assetStore.load<Pixmap>(path = handle.toString())
                    .crop(
                        MathUtils.ceil(width),
                        MathUtils.ceil(height),
                        level.backgroundAspectRatio
                    )

                drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap).apply {
                    setFilter(TextureFilter.Linear, TextureFilter.Linear)
                }))
                color = Color.WHITE
            }

        }

        table = scene2d.table {
            setFillParent(true)

            label(level.artist)
            defaults().row()
            label(level.title)
        }

        addActor(background)
        addActor(image)
        addActor(table)
    }

    override fun getPrefHeight(): Float {
        return 214f.scaledUi()
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