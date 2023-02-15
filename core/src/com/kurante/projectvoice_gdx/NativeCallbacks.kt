package com.kurante.projectvoice_gdx

import com.badlogic.gdx.files.FileHandle
import com.kurante.projectvoice_gdx.game.Conductor
import com.kurante.projectvoice_gdx.storage.StorageHandler

abstract class NativeCallbacks {
    abstract fun create(game: ProjectVoice)
    abstract fun dispose(game: ProjectVoice)
    abstract fun pause(game: ProjectVoice)
    abstract fun resume(game: ProjectVoice)
    abstract suspend fun loadConductor(game: ProjectVoice, handle: FileHandle): Conductor
    abstract fun getStorageHandler(): StorageHandler
}