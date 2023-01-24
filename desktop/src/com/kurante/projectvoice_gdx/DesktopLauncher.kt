package com.kurante.projectvoice_gdx

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        Lwjgl3Application(ProjectVoice(), Lwjgl3ApplicationConfiguration().apply {
            setForegroundFPS(60)
            setTitle("Project Voice")
            setWindowedMode(1280, 960)
            setWindowSizeLimits(640, 480, -1, -1)
        })
    }
}