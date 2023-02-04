package com.kurante.projectvoice_gdx.util

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.Hinting
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ktx.json.readValue
import ktx.json.setSerializer

// Implementation of SkinComposer's FreeType font exports in Kotlin
class ComposedSkin(handle: FileHandle) : Skin(handle) {
    override fun getJsonLoader(skinFile: FileHandle): Json {
        return super.getJsonLoader(skinFile).apply {
            setSerializer<FreeTypeFontGenerator>(FreeTypeSerializer(skinFile, this@ComposedSkin))
        }
    }
}

class FreeTypeSerializer(
    private val skinFile: FileHandle,
    private val skin: Skin,
) : Json.ReadOnlySerializer<FreeTypeFontGenerator>() {
    override fun read(json: Json, jsonData: JsonValue, type: Class<*>): FreeTypeFontGenerator? {
        val path = json.readValue<String>(jsonData, "font")
        jsonData.remove("font")

        val hinting = Hinting.valueOf(json.readValue(jsonData, "hinting", "AutoMedium"))
        jsonData.remove("hinting")

        val minFilter = TextureFilter.valueOf(json.readValue(jsonData, "minFilter", "Nearest"))
        jsonData.remove("minFilter")

        val magFilter = TextureFilter.valueOf(json.readValue(jsonData, "magFilter", "Nearest"))
        jsonData.remove("magFilter")

        val parameter = json.readValue<FreeTypeFontParameter>(jsonData).apply {
            this.hinting = hinting
            this.minFilter = minFilter
            this.magFilter = magFilter
        }

        val generator = FreeTypeFontGenerator(skinFile.parent().child(path))
        skin.add(jsonData.name, generator.generateFont(parameter))

        return if(parameter.incremental)
            generator
        else {
            generator.dispose()
            null
        }
    }

}

inline fun <reified T> Json.readValue(jsonData: JsonValue, name: String, defaultValue: T): T =
    readValue(name, T::class.java, defaultValue, jsonData)