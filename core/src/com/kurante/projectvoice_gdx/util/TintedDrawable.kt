package com.kurante.projectvoice_gdx.util

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.kurante.projectvoice_gdx.ui.widgets.MainColorElement

// https://gist.github.com/metaphore/67fb7ba95edb93b622c4
interface TintedDrawable : MainColorElement {
    var color: Color
    val mainColorChanged: (Color) -> Unit
        get() = { color = it }

    override fun setMainColor(enabled: Boolean) {
        if (enabled) {
            UserInterface.mainColorEvent += mainColorChanged
            color = UserInterface.mainColor
        } else
            UserInterface.mainColorEvent -= mainColorChanged
    }

    fun useBatchColor(batch: Batch, color: Color, callback: (Batch) -> Unit) {
        val original = batch.color
        batch.color = Color(color.r, color.g, color.b, batch.color.a)
        callback(batch)
        batch.color = original
    }
}

class TintedNinePatchDrawable : NinePatchDrawable, TintedDrawable {
    override var color: Color = Color.WHITE

    constructor(patch: NinePatch, mainColor: Boolean) : super(patch) { setMainColor(mainColor) }
    constructor(patch: NinePatch, color: Color) : super(patch) { this.color = color }

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) =
        useBatchColor(batch, color) { super.draw(it, x, y, width, height) }

    override fun draw(batch: Batch, x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float, rotation: Float) =
        useBatchColor(batch, color) { super.draw(it, x, y, originX, originY, width, height, scaleX, scaleY, rotation) }
}

class TintedTextureRegionDrawable : TextureRegionDrawable, TintedDrawable {
    override var color: Color = Color.WHITE

    constructor(region: TextureRegion, mainColor: Boolean) : super(region) { setMainColor(mainColor) }
    constructor(region: TextureRegion, color: Color) : super(region) { this.color = color }

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) =
        useBatchColor(batch, color) { super.draw(it, x, y, width, height) }

    override fun draw(batch: Batch, x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float, rotation: Float) =
        useBatchColor(batch, color) { super.draw(it, x, y, originX, originY, width, height, scaleX, scaleY, rotation) }
}