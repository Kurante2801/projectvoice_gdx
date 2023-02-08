package com.kurante.projectvoice_gdx.util

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ktx.json.setSerializer

class ComposedSkinFontless() : Skin() {
    override fun getJsonLoader(skinFile: FileHandle): Json {
        return super.getJsonLoader(skinFile).apply {
            setSerializer<FreeTypeFontGenerator>(FreeTypeSerializerDiscard())
        }
    }
}

class FreeTypeSerializerDiscard() : Json.ReadOnlySerializer<FreeTypeFontGenerator>() {
    override fun read(json: Json, jsonData: JsonValue, type: Class<*>): FreeTypeFontGenerator? {
        return null
    }
}