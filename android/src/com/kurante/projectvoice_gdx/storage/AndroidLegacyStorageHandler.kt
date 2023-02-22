package com.kurante.projectvoice_gdx.storage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.kurante.projectvoice_gdx.AndroidLauncher
import kurante.gdxscopedstorage.DocumentHandle

// For Android 10 or less
class AndroidLegacyStorageHandler(
    private val launcher: AndroidLauncher
) : StorageHandler {
    override fun requestFolderAccess(callback: (FileHandle?) -> Unit) {
        launcher.storageHandler.requestReadWritePermissions { success ->
            if (!success) return@requestReadWritePermissions callback(null)
            launcher.storageHandler.requestDocumentTree {
                if (it == null) return@requestDocumentTree callback(null)

                val document = it as DocumentHandle
                // We can use the File API on Android 10-
                callback(Gdx.files.absolute(document.realPath()))
            }
        }
    }
}