@file:Suppress("MemberVisibilityCanBePrivate")

package com.kurante.projectvoice_gdx.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.kurante.projectvoice_gdx.level.Level
import com.kurante.projectvoice_gdx.util.UserInterface
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import com.kurante.projectvoice_gdx.util.extensions.envelopeRatio
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.scene2d.*
import ktx.scene2d.Scene2DSkin.defaultSkin
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class LevelCard(
    val level: Level,
    private val assetStore: AssetStorage?,
) : Button(defaultSkin.getDrawable("white")) {
    @Suppress("JoinDeclarationAndAssignment")
    val image: Image
    val table: Table

    var backgroundBytes: Array<Byte>? = null
    var backgroundLoaded = false

    init {
        color = UserInterface.FOREGROUND1_COLOR

        image = Image(defaultSkin.getRegion("white")).apply {
            setFillParent(true)
            color = UserInterface.FOREGROUND1_COLOR

            if (assetStore == null || level.backgroundFilename == null) return@apply

            KtxAsync.launch {
                val handle = level.file.child(level.backgroundFilename)
                if (!handle.exists()) return@launch

                val tex = assetStore.load<Texture>(handle.path()).apply {
                    setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
                }

                drawable = TextureRegionDrawable(tex.envelopeRatio(level.backgroundAspectRatio))
                color = Color(0.75f, 0.75f, 0.75f, 1f)
            }

        }

        table = scene2d.table {
            setFillParent(true)

            label(level.artist)
            defaults().row()
            label(level.title) {
                style = LabelStyle(defaultSkin.getFont("bold"), null)
                setFontScale(1.25f)
            }
        }

        addActor(image)
        addActor(table)
    }

    override fun getPrefHeight(): Float {
        return 214f.scaledUi()
    }

    override fun getMinHeight(): Float {
        return width / (16f / 9f)
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