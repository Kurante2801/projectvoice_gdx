package com.kurante.projectvoice_gdx.storage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.kurante.projectvoice_gdx.storage.StorageManager.randomString
import com.kurante.projectvoice_gdx.storage.StorageManager.storageHandler
import ktx.app.Platform

// Copies file to Cache and returns the cache's file handle
class CacheFileHandleResolver : FileHandleResolver {
    override fun resolve(fileName: String): FileHandle {
        val file = storageHandler.fileFromString(fileName)

        var cached: FileHandle

        // local doesn't work on android
        if (Platform.isAndroid) {
            cached = Gdx.files.external("${randomString()}.${file.extension()}")
            while (cached.exists())
                cached = Gdx.files.external("${randomString()}.${file.extension()}")
        } else {
            cached = Gdx.files.local("${randomString()}.${file.extension()}")
            while (cached.exists())
                cached = Gdx.files.local("${randomString()}.${file.extension()}")
        }

        file.copyTo(cached)

        if(Platform.isAndroid) {
            cached = Gdx.files.absolute(Gdx.files.externalStoragePath + cached.name())
            Gdx.app.log("HELL", "cached: ${cached.path()} readable: ${cached.file().canRead()}")
        }

        return cached
    }
}