package com.kurante.projectvoice_gdx.storage

interface StorageHandler {
    fun requestFolderAccess(callback: (FileHandler?) -> Unit)
}