package com.kurante.projectvoice_gdx

import com.badlogic.gdx.files.FileHandle
import com.kurante.projectvoice_gdx.game.Conductor
import com.kurante.projectvoice_gdx.storage.StorageHandler

class IOSCallbacks : NativeCallbacks() {
    override fun create(game: ProjectVoice) {
        TODO("Not yet implemented")
    }

    override fun dispose(game: ProjectVoice) {
        TODO("Not yet implemented")
    }

    override fun pause(game: ProjectVoice) {
        TODO("Not yet implemented")
    }

    override fun resume(game: ProjectVoice) {
        TODO("Not yet implemented")
    }

    override suspend fun loadConductor(game: ProjectVoice, handle: FileHandle): Conductor {
        TODO("Not yet implemented")
    }

    override fun getStorageHandler(): StorageHandler {
        TODO("Not yet implemented")
    }
}