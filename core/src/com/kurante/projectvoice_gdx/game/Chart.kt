package com.kurante.projectvoice_gdx.game

data class Chart(
    val startTime: Int = 0,
    val musicOffset: Int = 0,
    val tracks: Array<Track> = arrayOf()
)

data class Track(
    val id: Int,
    val spawnTime: Int,
    val spawnDuration: Int,
    val despawnTime: Int,
    val despawnDuration: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return (other as Track).id == id
    }

    override fun hashCode(): Int = id.hashCode()
}