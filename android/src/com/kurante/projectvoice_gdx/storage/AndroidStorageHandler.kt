package com.kurante.projectvoice_gdx.storage

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.GdxRuntimeException
import com.kurante.projectvoice_gdx.AndroidLauncher
import kurante.gdxscopedstorage.DocumentHandle

class AndroidStorageHandler(
    private val launcher: AndroidLauncher
) : StorageHandler {
    override fun requestFolderAccess(callback: (FileHandle?) -> Unit) {
        launcher.storageHandler.requestDocumentTree(true, callback)
    }

    override fun fileFromString(string: String): FileHandle {
        return DocumentHandle.valueOf(launcher.context, string)
    }

    override fun directoryFromString(string: String): FileHandle {
        return DocumentHandle.valueOf(launcher.context, string)
    }

    override fun subDirectory(handle: FileHandle, name: String): FileHandle {
        if (handle !is DocumentHandle) return super.subDirectory(handle, name)

        val dir = handle.child(name) as DocumentHandle
        if (dir.exists()) return dir

        val child = dir.document.createDirectory(name)
            ?: throw GdxRuntimeException("Could not create subdirectory: $name")
        return DocumentHandle(launcher.context, child)
    }

    override fun isSAF(): Boolean = true

    override fun canRead(handle: FileHandle): Boolean {
        if (handle is DocumentHandle)
            return handle.document.canRead()
        return handle.file().canRead()
    }

    override fun canWrite(handle: FileHandle): Boolean {
        if (handle is DocumentHandle)
            return handle.document.canWrite()
        return handle.file().canWrite()
    }
}