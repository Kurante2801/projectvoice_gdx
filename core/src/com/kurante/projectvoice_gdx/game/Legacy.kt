package com.kurante.projectvoice_gdx.game

import com.badlogic.gdx.graphics.Color
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
                )
            )
        }

        return Chart(0, 0, tracks.toTypedArray())
    }

    fun convertTransitions(
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
                initialValue = transitions.last().endValue

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

        transitions.sortBy { it.startTime }
        return transitions.toTypedArray()
    }

    fun parseEase(ease: String): TransitionEase = when (ease) {
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
    val colors = arrayOf(
        "#F98F95",
        "#F9E5A1",
        "#D3D3D3",
        "#77D1DE",
        "#97D384",
        "#F3B67E",
        "#E2A0CB",
        "#8CBCE7",
        "#76DBCB",
        "#AEA6F0"
    )

    fun parseColor(value: Int): Color = Color.valueOf(colors.elementAtOrNull(value) ?: "#FFFFFFFF")
}

data class LegacyTrack(
    val Id: Int = 0,
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

data class LegacyTransition(
    val To: Float = 0f,
    val Ease: String = "easelinear",
    val Start: Float = 0f,
    val End: Float = 0f,
)