package com.kurante.projectvoice_gdx.storage

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.GdxRuntimeException
import com.kurante.projectvoice_gdx.AndroidLauncher
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext

class AndroidStorageHandler(
    private val launcher: AndroidLauncher
) : StorageHandler {
    override fun requestFolderAccess(callback: (FileHandle?) -> Unit) {
        val executor = newSingleThreadAsyncContext()
        KtxAsync.launch(executor) {
            launcher.openDocumentTree(callback)
        }
    }

    override fun fileFromString(string: String): FileHandle {
        return AndroidFileHandle(launcher, DocumentFile.fromSingleUri(launcher, Uri.parse(string))!!)
    }

    override fun directoryFromString(string: String): AndroidFileHandle {
        return AndroidFileHandle(launcher, DocumentFile.fromTreeUri(launcher, Uri.parse(string))!!)
    }

    override fun encode(string: String): String = Uri.encode(string)
    override fun decode(string: String): String = Uri.decode(string)

    override fun subDirectory(handle: FileHandle, name: String): FileHandle {
        var sub = handle.child(name)

        if(!sub.exists()) {
            sub = (handle as AndroidFileHandle).createDirectory(name)
                ?: throw GdxRuntimeException("(Android) Could not get nor create subdirectory $name on ${handle.name()}")
        }

        return sub
    }

    override fun subFile(handle: FileHandle, name: String): FileHandle {
        var sub = handle.child(name)

        if(!sub.exists()) {
            sub = (handle as AndroidFileHandle).createFile(name)
                ?: throw GdxRuntimeException("(Android) Could not get nor create subdirectory $name on ${handle.name()}")
        }

        return sub
    }
}