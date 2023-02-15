package com.kurante.projectvoice_gdx

import com.badlogic.gdx.files.FileHandle
import com.kurante.projectvoice_gdx.game.Conductor
import com.kurante.projectvoice_gdx.storage.StorageHandler
import com.kurante.projectvoice_gdx.storage.WindowsStorageHandler
import games.rednblack.miniaudio.MiniAudio
import games.rednblack.miniaudio.loader.MASoundLoader
import games.rednblack.miniaudio.loader.MASoundLoaderParameters

class DesktopCallbacks : NativeCallbacks() {
    lateinit var miniAudio: MiniAudio

    override fun create(game: ProjectVoice) {
        miniAudio = MiniAudio()
        game.absoluteStorage.setLoader {
            MASoundLoader(miniAudio, game.absoluteStorage.fileResolver)
        }
    }

    override fun pause(game: ProjectVoice) {
        miniAudio.stopEngine()
    }

    override fun resume(game: ProjectVoice) {
        miniAudio.startEngine()
    }

    override fun dispose(game: ProjectVoice) {
        miniAudio.dispose()
    }

    override suspend fun loadConductor(game: ProjectVoice, handle: FileHandle): Conductor {
        val sound = game.absoluteStorage.load(handle.path(), MASoundLoaderParameters().apply {
            external = true
        })
        return MiniAudioConductor(game.absoluteStorage, handle, sound)
    }

    override fun getStorageHandler(): StorageHandler = WindowsStorageHandler()
}