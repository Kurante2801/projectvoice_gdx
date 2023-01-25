package com.kurante.projectvoice_gdx.level

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.GdxRuntimeException
import kotlin.math.min

// todo: localized variables
data class Level (
    val file: FileHandle,
    val id: String,
    val title: String,
    val artist: String,

    val musicFilename: String,
    val previewFilename: String? = null,
    val previewTime: Int? = null,

    val backgroundFilename: String,
    val backgroundAspectRatio: Float,
    val charts: Array<ChartSection>
) {
    companion object {
        private val audioExtensions = arrayOf("mp3", "wav", "ogg")

        // Parses a legacy songconfig.txt
        fun fromSongConfig(directory: FileHandle): Level {
            var musicFile: FileHandle? = null
            var previewFile: FileHandle? = null

            for(file in directory.list()) {
                if(file.extension() !in audioExtensions) continue

                when(file.nameWithoutExtension()) {
                    "song_full" -> musicFile = file
                    "song_pv" -> previewFile = file
                }
            }

            if(musicFile == null)
                throw GdxRuntimeException("Music file not found: ${directory.name()}")

            // songconfig.txt is like an INI file
            val songConfig = directory.child("songConfig.txt")
            assert(songConfig.exists())

            // Each line will be: key=value
            val data = mutableMapOf<String, String>()
            for(line in songConfig.readString().split("\n")) {
                val index = line.lastIndexOf('=')
                if(index < 0) continue
                data[line.substring(0, index)] = line.substring(index + 1)
            }

            val diff = data["diff"]
            val id = data["id"]
            val name = data["name"]
            val author = data["author"]

            if(diff == null) throw GdxRuntimeException("Config file does not have 'diff' defined: ${directory.name()}")
            if(id == null) throw GdxRuntimeException("Config file does not have 'id' defined: ${directory.name()}")
            if(name == null) throw GdxRuntimeException("Config file does not have 'name' defined: ${directory.name()}")
            if(author == null) throw GdxRuntimeException("Config file does not have 'author' defined: ${directory.name()}")

            val diffs = diff.split('-')
            if(diffs.isEmpty()) throw GdxRuntimeException("Could not parse difficulties: ${directory.name()}")

            val charts = mutableListOf<ChartSection>()
            for(i in 0..min(diffs.size - 1, 5)) {
                val difficulty = diffs[i].toIntOrNull() ?: 0
                if(difficulty <= 0) continue

                val type = when (i) {
                    0 -> DifficultyType.EASY
                    1 -> DifficultyType.HARD
                    else -> DifficultyType.EXTRA
                }

                // If we're adding additional difficulties, start enumeration from track_extra2.json
                val path = if (i <= 2) "track_${type.toString().lowercase()}.json" else "track_extra${i - 1}.json"

                charts.add(
                    ChartSection(
                    chartFilename = path,
                    difficulty = difficulty,
                    type = type
                )
                )
            }

            if(charts.isEmpty())
                throw GdxRuntimeException("Level does not have any charts: ${directory.name()}")

            return Level(
                file = directory,
                id = id,
                title = name,
                artist = author,
                musicFilename = musicFile.name(),
                previewFilename = previewFile?.name(),
                previewTime = data["preview_time"]?.toIntOrNull(),
                backgroundFilename = data["background_name"] ?: "image_regular.png",
                backgroundAspectRatio = data["background_aspect"]?.toFloatOrNull() ?: (4f / 3f),
                charts = charts.toTypedArray()
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return (other as Level).id == id
    }

    override fun hashCode(): Int = id.hashCode()
}