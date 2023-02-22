package com.kurante.projectvoice_gdx.storage

import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle

class StorageFileHandleResolver(val storageHandler: StorageHandler) : FileHandleResolver {
    override fun resolve(fileName: String): FileHandle {
        return storageHandler.fileFromString(fileName)
    }
}