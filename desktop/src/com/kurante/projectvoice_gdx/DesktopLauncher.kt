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
    @JvmStatic
    fun main(arg: Array<String>) {
        Lwjgl3Application(ProjectVoice(DesktopCallbacks()), Lwjgl3ApplicationConfiguration().apply {
            setForegroundFPS(144)
            setTitle("Project Voice")
            setWindowedMode(1280, 720)
            setWindowSizeLimits(640, 480, -1, -1)
        })
    }
}