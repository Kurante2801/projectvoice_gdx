package com.kurante.projectvoice_gdx.storage

interface FileHandler {
    val path: String
    val encodedPath: String

    override fun toString(): String

    fun getBytes(): ByteArray
    fun getFiles(): Array<FileHandler>
}