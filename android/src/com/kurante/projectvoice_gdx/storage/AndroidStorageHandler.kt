package com.kurante.projectvoice_gdx.storage

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.badlogic.gdx.files.FileHandle
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

    override fun fromString(string: String): AndroidFileHandle? {
        var doc = DocumentFile.fromSingleUri(launcher, Uri.parse(string))
            ?: return null

        // If directory, convert to tree
        if(doc.isDirectory)
           doc = DocumentFile.fromTreeUri(launcher, Uri.parse(string))
               ?: return null

        return AndroidFileHandle(launcher, doc)
    }

    override fun encode(string: String): String = Uri.encode(string)
    override fun decode(string: String): String = Uri.decode(string)
}