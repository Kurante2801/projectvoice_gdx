package com.kurante.projectvoice_gdx.storage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle

interface StorageHandler {
    fun requestFolderAccess(callback: (FileHandle?) -> Unit)

    fun fileFromString(string: String): FileHandle {
        return Gdx.files.absolute(string)
    }

    fun directoryFromString(string: String): FileHandle {
        return Gdx.files.absolute(string)
    }

    fun encode(string: String): String = string
    fun decode(string: String): String = string

    fun subDirectory(handle: FileHandle, name: String): FileHandle {
        val dir = handle.child(name)
        dir.mkdirs()
        return dir
    }

    fun subFile(handle: FileHandle, name: String): FileHandle {
        val file = handle.child(name)
        if (!file.exists())
            file.writeString("", true)
        return file
    }

    fun isSAF(): Boolean = false
}