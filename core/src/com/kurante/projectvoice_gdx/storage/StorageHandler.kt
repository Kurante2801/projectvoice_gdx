package com.kurante.projectvoice_gdx.storage

import com.badlogic.gdx.files.FileHandle

interface StorageHandler {
    fun requestFolderAccess(callback: (FileHandle?) -> Unit)

    fun fileFromString(string: String): FileHandle
    fun directoryFromString(string: String): FileHandle

    fun encode(string: String): String
    fun decode(string: String): String
}