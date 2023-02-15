package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.kurante.projectvoice_gdx.storage.StorageManager.randomString

fun FileHandle.copyToCache(): FileHandle {
    var output = Gdx.files.local("cache/${randomString()}.${this.extension()}")
    while (output.exists())
        output = Gdx.files.local("cache/${randomString()}.${this.extension()}")
    this.copyTo(output)
    return output
}

fun FileHandle.absoluteFromLocal(): FileHandle {
    return Gdx.files.absolute(Gdx.files.localStoragePath + path())
}
