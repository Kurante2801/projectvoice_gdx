package com.kurante.projectvoice_gdx.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Json
import com.kurante.projectvoice_gdx.game.*
import com.kurante.projectvoice_gdx.level.ChartSection
import com.kurante.projectvoice_gdx.level.DifficultyType
import com.kurante.projectvoice_gdx.level.Level
import com.kurante.projectvoice_gdx.util.extensions.capitalize
import com.kurante.projectvoice_gdx.util.extensions.toMillis
import com.kurante.projectvoice_gdx.game.Track.Transition
import com.kurante.projectvoice_gdx.game.Track.ColorTransition
import ktx.json.fromJson
import kotlin.math.min

object LegacyParser {
    private val audioExtensions = arrayOf("mp3", "wav", "ogg")

    fun parseLevel(directory: FileHandle, config: FileHandle): Level {
        var musicFile: FileHandle? = null
        var previewFile: FileHandle? = null
        // Find any audio file
        for (file in directory.list()) {
            if (file.extension() !in audioExtensions) continue
            when (file.nameWithoutExtension()) {
                "song_full" -> musicFile = file
                "song_pv" -> previewFile = file
            }
        }

        if (musicFile == null) throw GdxRuntimeException("Music file not found: ${directory.name()}")

        // songconfig.txt is structured as: key=value
        val data = mutableMapOf<String, String>()
        for (line in config.readString().split("\r\n", "\r", "\n")) {
            val index = line.lastIndexOf('=')
            if (index < 0) continue
            data[line.substring(0, index)] = line.substring(index + 1)
        }

        val diff = data["diff"] ?: throw GdxRuntimeException("Config file does not have 'diff' defined: ${directory.name()}")
        val id = data["id"] ?: throw GdxRuntimeException("Config file does not have 'id' defined: ${directory.name()}")
        val name = data["name"] ?: throw GdxRuntimeException("Config file does not have 'name' defined: ${directory.name()}")
        val author = data["author"] ?: throw GdxRuntimeException("Config file does not have 'author' defined: ${directory.name()}")

        val diffs = diff.split('-')
        if (diffs.isEmpty()) throw GdxRuntimeException("Could not parse difficulties: ${directory.name()}")

        val charts = mutableListOf<ChartSection>()
        val count = min(diffs.size, 6) // Limit charts to 6 per level
        for (i in 0 until count) {
            val difficulty = diffs[i].toIntOrNull() ?: 0
            if (difficulty <= 0) {
                Gdx.app.log("LegacyParser", "Skipped difficulty i: $i because it had a difficulty of 0 or below")
                continue
            }

            val type = when (i) {
                0 -> DifficultyType.EASY
                1 -> DifficultyType.HARD
                else -> DifficultyType.EXTRA
            }

            // If we're adding additional difficulties, start enumeration from track_extra2.json
            val path = if (i <= 2) "track_${type.toString().lowercase()}.json" else "track_extra${i - 1}.json"

            charts.add(ChartSection(path, difficulty, type, type.toString().lowercase().capitalize(), null))
        }

        if (charts.isEmpty()) throw GdxRuntimeException("Level does not have any charts: ${directory.name()}")

        return Level(
            file = directory,
            id = id,
            title = name,
            artist = author,
            musicFilename = musicFile.name(),
            previewFilename = previewFile?.name(),
            previewTime = data["preview_time"]?.toIntOrNull(),
            backgroundFilename = data["background_name"] ?: "image_regular.png",
            backgroundAspectRatio = data["background_ratio"]?.toFloatOrNull() ?: 1.25f,
            charts = charts.toTypedArray(),
        )
    }

    fun parseChart(level: Level, section: ChartSection): Chart {
        val json = Json()
        json.ignoreUnknownFields = true

        val legacyTracks = json.fromJson<Array<LegacyTrack>>(level.file.child(section.chartFilename))
        val legacyNotes = json.fromJson<Array<LegacyNote>>(level.file.child(section.chartFilename.replace("track_", "note_")))
        val tracks = mutableListOf<Track>()

        // Sort the same way as editor
        legacyTracks.sortBy { it.Start }
        legacyNotes.sortBy { it.Time }

        for (note in legacyNotes) {
            for (i in legacyTracks.indices) {
                if (note.Track == legacyTracks[i].Id) {
                    note.Track = i
                }
            }
        }
        for (i in legacyTracks.indices)
            legacyTracks[i].Id = i

        for (legacyTrack in legacyTracks) {
            val moveTransitions = convertTransitions(
                legacyTrack,
                legacyTrack.Move,
                legacyTrack.X,
                TransitionEase.EXIT_MOVE
            )
            val scaleTransitions = convertTransitions(
                legacyTrack,
                legacyTrack.Scale,
                legacyTrack.Size,
                TransitionEase.EXIT_SCALE
            )

            val colorTransitions = convertTransitions(
                legacyTrack,
                legacyTrack.ColorChange,
                legacyTrack.Color,
                TransitionEase.EXIT_COLOR
            ).map {
                ColorTransition(
                    startTime = it.startTime,
                    endTime = it.endTime,
                    startValue = parseColor(it.startValue.toInt()),
                    endValue = parseColor(it.endValue.toInt()),
                    easing = it.easing,
                )
            }.toTypedArray()

            val notes = mutableListOf<Note>()
            for (note in legacyNotes) {
                if (note.Track != legacyTrack.Id) continue
                val type = parseNoteType(note.Type)
                val data = when(type) {
                    NoteType.HOLD -> note.Hold.toMillis()
                    NoteType.SWIPE -> if (note.Dir <= 0) -1 else 1
                    else -> 0
                }
                notes.add(Note(note.Id, note.Time.toMillis(), type, data))
            }

            tracks.add(
                Track(
                    id = legacyTrack.Id,
                    spawnTime = legacyTrack.Start.toMillis(),
                    despawnTime = legacyTrack.End.toMillis(),
                    despawnDuration = 250,
                    spawnDuration = if (legacyTrack.EntranceOn) 350 else 0,
                    moveTransitions = moveTransitions,
                    scaleTransitions = scaleTransitions,
                    colorTransitions = colorTransitions,
                    notes = notes.toTypedArray()
                )
            )
        }

        return Chart(0, null, 0, tracks.toTypedArray())
    }

    private fun convertTransitions(
        legacyTrack: LegacyTrack,
        legacy: Array<LegacyTransition>,
        initial: Float,
        exit: TransitionEase,
    ): Array<Transition> {
        var initialValue = initial
        val transitions = mutableListOf<Transition>()

        // No transitions, so we create one transition with just the initial value
        if (legacy.isEmpty()) {
            transitions.add(
                Transition(
                    startTime = legacyTrack.Start.toMillis(),
                    endTime = legacyTrack.End.toMillis(),
                    startValue = initialValue,
                    endValue = initialValue,
                    easing = TransitionEase.NONE,
                )
            )

            return transitions.toTypedArray()
        }

        // Fix gaps from spawn to initial transition
        var transition = legacy[0]
        if (transition.Start.toMillis() != legacyTrack.Start.toMillis()) {
            transitions.add(
                Transition(
                    startTime = legacyTrack.Start.toMillis(),
                    endTime = transition.Start.toMillis(),
                    startValue = initialValue,
                    endValue = initialValue,
                    easing = TransitionEase.NONE,
                )
            )
        }

        // Convert transitions
        for (i in legacy.indices) {
            transition = legacy[i]
            // If this is not the first legacy transition, we get the value from the previous transition
            if (i > 0)
                initialValue = transitions[transitions.size - 1].endValue

            // EXIT transition is different for move, scale and color
            var ease = parseEase(transition.Ease)
            if (ease == TransitionEase.EXIT)
                ease = exit

            transitions.add(
                Transition(
                    startTime = transition.Start.toMillis(),
                    endTime = transition.End.toMillis(),
                    startValue = initialValue,
                    endValue = transition.To,
                    easing = ease
                )
            )
        }

        return transitions.toTypedArray().apply { sortBy { it.startTime } }
    }

    private fun parseEase(ease: String): TransitionEase = when (ease) {
        "easelinear" -> TransitionEase.LINEAR
        "easeinquad" -> TransitionEase.QUAD_IN
        "easeoutquad" -> TransitionEase.QUAD_OUT
        "easeinoutquad" -> TransitionEase.QUAD_INOUT
        "easeoutinquad" -> TransitionEase.QUAD_OUTIN
        "easeincirc" -> TransitionEase.CIRC_IN
        "easeoutcirc" -> TransitionEase.CIRC_OUT
        "easeinoutcirc" -> TransitionEase.CIRC_INOUT
        "easeoutincirc" -> TransitionEase.CIRC_OUTIN
        "easeinexpo" -> TransitionEase.EXP_IN
        "easeoutexpo" -> TransitionEase.EXP_OUT
        "easeinoutexpo" -> TransitionEase.EXP_INOUT
        "easeoutinexpo" -> TransitionEase.EXP_OUTIN
        "easeinback" -> TransitionEase.BACK_IN
        "easeoutback" -> TransitionEase.BACK_OUT
        "easeinoutback" -> TransitionEase.EXIT // As parsed by editor
        "easeoutinback" -> TransitionEase.BACK_OUTIN
        "easeintelastic" -> TransitionEase.ELASTIC_IN // The 'T' in easeinTelastic is not a typo on my part
        "easeoutelastic" -> TransitionEase.ELASTIC_OUT
        "easeinoutelastic" -> TransitionEase.ELASTIC_INOUT
        "easeoutinelastic" -> TransitionEase.ELASTIC_OUTIN
        else -> TransitionEase.NONE
    }

    // In legacy, colors are stored as numbers that map to these values
    // https://github.com/AndrewFM/VoezEditor/blob/master/Assets/Scripts/ProjectData.cs#L548
    private val colors = arrayOf(
        Color.valueOf("#F98F95")!!,
        Color.valueOf("#F9E5A1")!!,
        Color.valueOf("#D3D3D3")!!,
        Color.valueOf("#77D1DE")!!,
        Color.valueOf("#97D384")!!,
        Color.valueOf("#F3B67E")!!,
        Color.valueOf("#E2A0CB")!!,
        Color.valueOf("#8CBCE7")!!,
        Color.valueOf("#76DBCB")!!,
        Color.valueOf("#AEA6F0")!!,
    )
    private fun parseColor(value: Int): Color = colors.elementAtOrNull(value) ?: Color(1f, 1f, 1f, 1f)

    private fun parseNoteType(type: String): NoteType = when(type) {
        "swipe" -> NoteType.SWIPE
        "hold" -> NoteType.HOLD
        "slide" -> NoteType.SLIDE
        else -> NoteType.CLICK
    }

    private data class LegacyTrack(
        var Id: Int = 0,
        val EntranceOn: Boolean = true,
        val X: Float = 0f,
        val Size: Float = 1f,
        val Start: Float = 0f,
        val End: Float = 10f,
        val Color: Float = 1f,
        val Move: Array<LegacyTransition> = arrayOf(),
        val Scale: Array<LegacyTransition> = arrayOf(),
        val ColorChange: Array<LegacyTransition> = arrayOf(),
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return (other as LegacyTrack).Id == Id
        }

        override fun hashCode(): Int = Id.hashCode()
    }

    private data class LegacyTransition(
        val To: Float = 0f,
        val Ease: String = "easelinear",
        val Start: Float = 0f,
        val End: Float = 0f,
    )

    private data class LegacyNote(
        val Id: Int = 0,
        val Type: String = "click",
        var Track: Int = 0,
        val Time: Float = 0f,
        val Hold: Float = 0f,
        val Dir: Int = 0,
    )
}