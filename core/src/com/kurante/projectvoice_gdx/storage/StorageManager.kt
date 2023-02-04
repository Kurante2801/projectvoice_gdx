package com.kurante.projectvoice_gdx.storage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.kurante.projectvoice_gdx.util.extensions.random

object StorageManager {
    lateinit var storageHandler: StorageHandler

    val chars = "abcdefghijklmnopqrstuvwxyz".toCharArray()

    fun randomString(length: Int = 6): String {
        val result = CharArray(length)

        for (i in 0 until length)
            result[i] = chars.random()

        return result.joinToString("")
    }

    // Necessary for MiniAudio on Android!
    fun copyToLocal(file: FileHandle): FileHandle {
        var output = Gdx.files.local("${randomString()}.${file.extension()}")
        while (output.exists())
            output = Gdx.files.local("${randomString()}.${file.extension()}")
        file.copyTo(output)
        return Gdx.files.absolute(Gdx.files.localStoragePath + output.path())
    }
}