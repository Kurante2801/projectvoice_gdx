package com.kurante.projectvoice_gdx.storage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.kurante.projectvoice_gdx.util.extensions.random

object StorageManager {
    lateinit var storageHandler: StorageHandler
    lateinit var cachePath: String
    lateinit var cache: FileHandle

    val chars = "abcdefghijklmnopqrstuvwxyz".toCharArray()

    fun randomString(length: Int = 6): String {
        val result = CharArray(length)

        for (i in 0 until length)
            result[i] = chars.random()

        return result.joinToString("")
    }
}