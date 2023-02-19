package com.kurante.projectvoice_gdx.game

data class Note(
    val id: Int,
    val time: Int,
    val type: NoteType,
    val data: Int,
) {
    companion object {
        // https://github.com/AndrewFM/VoezEditor/blob/master/Assets/Scripts/Note.cs#L18
        val scrollDurations = arrayOf(
            1500, // 1x
            1300, // 2x
            1100, // 3x
            900, // 4x
            800, // 5x
            700, // 6x
            550, // 7x
            425, // 8x
            300, // 9x
            200, // 10x
        )
    }
}