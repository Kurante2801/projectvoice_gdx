package com.kurante.projectvoice_gdx.storage

import com.badlogic.gdx.files.FileHandle
import com.kurante.projectvoice_gdx.AndroidLauncher

class AndroidLegacyStorageHandler(
    private val launcher: AndroidLauncher
) : StorageHandler {
    override fun requestFolderAccess(callback: (FileHandle?) -> Unit) {
        launcher.openDocumentTreeLegacy(callback)
    }
}