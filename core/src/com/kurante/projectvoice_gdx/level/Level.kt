package com.kurante.projectvoice_gdx.level

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.GdxRuntimeException
import kotlin.math.min

// todo: localized variables
data class  Level(
    val file: FileHandle,
    val id: String,
    val title: String,
    val artist: String,

    val musicFilename: String,
    val previewFilename: String? = null,
    val previewTime: Int? = null,

    val backgroundFilename: String? = null,
    val backgroundAspectRatio: Float?,
    val charts: Array<ChartSection>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return (other as Level).id == id
    }

    override fun hashCode(): Int = id.hashCode()
}