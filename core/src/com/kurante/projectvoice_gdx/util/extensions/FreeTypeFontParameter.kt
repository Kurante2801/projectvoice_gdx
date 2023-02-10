package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun FreeTypeFontParameter.copy(
    block: FreeTypeFontParameter.() -> Unit = {}
): FreeTypeFontParameter {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

    val original = this
    val copy = FreeTypeFontParameter().apply {
        size = original.size
        mono = original.mono
        hinting = original.hinting
        color = original.color
        gamma = original.gamma
        renderCount = original.renderCount
        borderWidth = original.borderWidth
        borderColor = original.borderColor
        borderStraight = original.borderStraight
        borderGamma = original.borderGamma
        shadowOffsetX = original.shadowOffsetX
        shadowOffsetY = original.shadowOffsetY
        shadowColor = original.shadowColor
        spaceX = original.spaceX
        spaceY = original.spaceY
        padTop = original.padTop
        padLeft = original.padLeft
        padBottom = original.padBottom
        padRight = original.padRight
        characters = original.characters
        kerning = original.kerning
        packer = original.packer
        flip = original.flip
        genMipMaps = original.genMipMaps
        minFilter = original.minFilter
        magFilter = original.magFilter
        incremental = original.incremental

        block(this)
    }

    return copy
}