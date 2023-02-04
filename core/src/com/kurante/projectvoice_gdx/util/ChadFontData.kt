package com.kurante.projectvoice_gdx.util

import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData

// Allows for font fallbacks (they must all use the same PixmapPacker)
class ChadFontData(
    private val generator: FreeTypeFontGenerator,
) : FreeTypeBitmapFontData() {
    val fallbackFonts = mutableListOf<ChadFontData>()

    override fun getGlyph(char: Char): Glyph? {
        if (generator.hasGlyph(char.code))
            return super.getGlyph(char)
        else {
            for (fallback in fallbackFonts) {
                if (fallback.generator.hasGlyph(char.code))
                    return fallback.getGlyph(char)
            }
        }

        return null
    }
}