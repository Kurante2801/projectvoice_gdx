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
        val doc = DocumentFile.fromSingleUri(launcher, Uri.parse(string))
        return if(doc != null) AndroidFileHandle(launcher, doc) else null
    }

    override fun encode(string: String): String = Uri.encode(string)
    override fun decode(string: String): String = Uri.decode(string)
}