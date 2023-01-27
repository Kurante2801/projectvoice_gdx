package com.kurante.projectvoice_gdx.storage

import com.badlogic.gdx.files.FileHandle

interface StorageHandler {
    fun requestFolderAccess(callback: (FileHandle?) -> Unit)

    fun fileFromString(string: String): FileHandle
    fun directoryFromString(string: String): FileHandle

    fun encode(string: String): String
    fun decode(string: String): String

    fun subdirectory(handle: FileHandle, name: String): FileHandle
    fun subfile(handle: FileHandle, name: String): FileHandle
}