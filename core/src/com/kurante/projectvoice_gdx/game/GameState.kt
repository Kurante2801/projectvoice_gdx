package com.kurante.projectvoice_gdx.game

import com.badlogic.gdx.Gdx
import com.kurante.projectvoice_gdx.level.ChartSection
import com.kurante.projectvoice_gdx.level.Level
import com.kurante.projectvoice_gdx.util.extensions.mapRange
import kotlin.math.max
import kotlin.math.min

@Suppress("ConvertSecondaryConstructorToPrimary")
class GameState {
    data class JudgeData(var grade: NoteGrade?, var error: Int)

    companion object {
        const val MILLION: Double = 1000000.0
    }

    val level: Level
    val section: ChartSection
    val chart: Chart
    var completed: Boolean = false

    private val judgements: Map<Int, JudgeData>
    var score: Double private set
    var accuracy: Double private set
    val accuracies = mutableListOf<Double>()
    var combo: Int private set
    var maxCombo: Int private set
    var noteCount: Int private set
    var clearCount: Int private set

    var isFullScorePossible: Boolean private set
    var isFullComboPossible: Boolean private set

    constructor(level: Level, section: ChartSection, chart: Chart) {
        this.level = level
        this.section = section
        this.chart = chart

        val judgements = mutableMapOf<Int, JudgeData>()
        for (track in chart.tracks) {
            for (note in track.notes)
                judgements[note.id] = JudgeData(null, 0)
        }
        this.judgements = judgements.toMap()

        score = 0.0
        accuracy = 0.0
        combo = 0
        maxCombo = 0
        noteCount = 0
        clearCount = 0

        isFullComboPossible = true
        isFullScorePossible = true
    }

    fun judge(note: Note, grade: NoteGrade, difference: Int) {
        if (completed || isNoteJudged(note.id)) return

        judgements[note.id]!!.grade = grade
        judgements[note.id]!!.error = difference

        if (grade != NoteGrade.PERFECT)
            isFullScorePossible = false
        if (grade == NoteGrade.MISS) {
            isFullComboPossible = false
            combo = 0
        } else
            combo++

        // If player missed a note and is below their max combo, award 90% of total score
        val comboMultiplier = if (combo > maxCombo) 1f else 0.9f

        // In addition, multiply score by the accuracy they had
        val accuracyMultiplier = when(grade) {
            NoteGrade.PERFECT -> 1.0
            NoteGrade.GREAT -> difference.mapRange(NoteGrade.GREAT.timing, NoteGrade.PERFECT.timing, 0.708, 0.9)
            NoteGrade.GOOD -> difference.mapRange(NoteGrade.GOOD.timing, NoteGrade.GREAT.timing, 0.204, 0.7)
            else -> 0.0
        }

        maxCombo = max(combo, maxCombo)
        clearCount++

        // Accuracy percentage
        accuracies.add(grade.weight)
        accuracy = 0.0
        accuracies.forEach { accuracy += it }
        accuracy /= accuracies.size

        score = min(score + MILLION / noteCount * grade.weight * comboMultiplier * accuracyMultiplier, MILLION)

        // Ensure million score
        if (clearCount == noteCount && isFullScorePossible)
            score = MILLION

        if (grade != NoteGrade.PERFECT)
            Gdx.app.log("GameState", "ID: ${note.id} NOTE TIME: ${note.time} ms DIFFERENCE: $difference ms")
    }

    fun isNoteJudged(id: Int) = judgements[id]!!.grade != null
}
