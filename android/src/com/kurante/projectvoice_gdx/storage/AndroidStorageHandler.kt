package com.kurante.projectvoice_gdx.storage

import com.kurante.projectvoice_gdx.AndroidLauncher

class AndroidStorageHandler(
    private val launcher: AndroidLauncher
) : StorageHandler {
    override fun requestFolderAccess(callback: (FileHandler?) -> Unit) {
        launcher.openDocumentTree(callback)
    }
}