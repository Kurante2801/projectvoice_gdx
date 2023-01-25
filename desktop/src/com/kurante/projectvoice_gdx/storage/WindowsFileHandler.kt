package com.kurante.projectvoice_gdx.storage

import com.badlogic.gdx.files.FileHandle

class WindowsFileHandler(
    private val handle: FileHandle
) : FileHandler {
    override val path: String = handle.path()
    override val encodedPath: String = handle.path()

    override fun toString(): String = handle.path()
    override fun getBytes(): ByteArray = handle.readBytes()
    override fun getFiles(): Array<FileHandler> = handle.list().map { WindowsFileHandler(it) }.toTypedArray()
}