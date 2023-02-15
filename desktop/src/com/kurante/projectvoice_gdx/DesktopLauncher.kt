package com.kurante.projectvoice_gdx

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.files.FileHandle
import com.kurante.projectvoice_gdx.game.Conductor
import com.kurante.projectvoice_gdx.storage.StorageManager
import com.kurante.projectvoice_gdx.storage.WindowsStorageHandler
import com.kurante.projectvoice_gdx.util.extensions.toMillis
import games.rednblack.miniaudio.MASound
import games.rednblack.miniaudio.MiniAudio
import games.rednblack.miniaudio.loader.MASoundLoader
import games.rednblack.miniaudio.loader.MASoundLoaderParameters
import kotlin.math.min

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
object DesktopLauncher {
    lateinit var miniAudio: MiniAudio

    class DesktopCallbacks : NativeCallbacks() {
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
    }


    @JvmStatic
    fun main(arg: Array<String>) {
        // Native code
        StorageManager.storageHandler = WindowsStorageHandler()

        Lwjgl3Application(ProjectVoice(DesktopCallbacks()), Lwjgl3ApplicationConfiguration().apply {
            setForegroundFPS(144)
            setTitle("Project Voice")
            setWindowedMode(1280, 960)
            setWindowSizeLimits(640, 480, -1, -1)
        })
    }
}