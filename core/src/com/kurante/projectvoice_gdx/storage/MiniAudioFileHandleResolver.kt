package com.kurante.projectvoice_gdx.storage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.kurante.projectvoice_gdx.storage.StorageManager.randomString
import com.kurante.projectvoice_gdx.storage.StorageManager.storageHandler
import ktx.app.Platform

// MiniAudio requires an absolute FileHandle
// since we can't provide that with SAF, we just copy the file
// to our data folder and return the handle for the copy
class MiniAudioFileHandleResolver : FileHandleResolver {
    override fun resolve(fileName: String): FileHandle {
        if(!Platform.isAndroid)
            return Gdx.files.absolute(fileName)

        val file = storageHandler.fileFromString(fileName)

        var cached = Gdx.files.local("${randomString()}.${file.extension()}")
        while (cached.exists())
            cached = Gdx.files.local("${randomString()}.${file.extension()}")

        file.copyTo(cached)

        return Gdx.files.absolute(Gdx.files.localStoragePath + cached.path())
    }
}