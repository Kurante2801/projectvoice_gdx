package com.kurante.projectvoice_gdx.game

import com.badlogic.gdx.utils.Json
import com.kurante.projectvoice_gdx.level.ChartSection
import com.kurante.projectvoice_gdx.level.Level
import com.kurante.projectvoice_gdx.util.extensions.toMillis
import ktx.json.fromJson

object Legacy {
    fun parseChart(level: Level, section: ChartSection): Chart {
        val json = Json()
        json.ignoreUnknownFields = true

        val legacyTracks = json.fromJson<Array<LegacyTrack>>(level.file.child(section.chartFilename))
        val tracks = mutableListOf<Track>()

        // Sort the same way as editor
        legacyTracks.sortBy { it.Start }

        for (legacy in legacyTracks) {
            tracks.add(
                Track(
                    id = legacy.Id,
                    spawnTime = legacy.Start.toMillis(),
                    despawnTime = legacy.End.toMillis(),
                    despawnDuration = 250,
                    spawnDuration = if (legacy.EntranceOn) 350 else 0
                )
            )
        }

        return Chart(0, 0, tracks.toTypedArray())
    }
}

data class LegacyTrack(
    val Id: Int = 0,
    val EntranceOn: Boolean = true,
    val X: Float = 0f,
    val Size: Float = 1f,
    val Start: Float = 0f,
    val End: Float = 10f,
    val Color: Int = 1,
    val Move: Array<LegacyTransition> = arrayOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return (other as LegacyTrack).Id == Id
    }

    override fun hashCode(): Int = Id.hashCode()
}

data class LegacyTransition(
    val To: Float = 0f,
    val Ease: String = "linear",
    val Start: Float = 0f,
    val End: Float = 0f,
)