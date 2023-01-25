package com.kurante.projectvoice_gdx.storage

import com.badlogic.gdx.files.FileHandle

interface StorageHandler {
    fun requestFolderAccess(callback: (FileHandle?) -> Unit)

    fun fromString(string: String): FileHandle?

    fun encode(string: String): String
    fun decode(string: String): String
}