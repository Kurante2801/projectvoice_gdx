package com.kurante.projectvoice_gdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Disposable
import com.kurante.projectvoice_gdx.storage.StorageManager
import com.kurante.projectvoice_gdx.util.extensions.isAndroidSAF
import games.rednblack.miniaudio.MASound
import games.rednblack.miniaudio.loader.MASoundLoaderParameters
import kotlinx.coroutines.launch
import ktx.app.Platform
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync

class Conductor(
    private val assetStorage: AssetStorage, // Must be absolute for MiniAudio to load!!
    handle: FileHandle,
) : Disposable {
    val file = if(Platform.isAndroidSAF) StorageManager.copyToCache(handle) else handle
    lateinit var sound: MASound
    var loaded = false

    init {
        KtxAsync.launch {
            try {
                sound = assetStorage.load(file.path(), MASoundLoaderParameters().apply {
                    external = true
                })
                loaded = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun dispose() {
        Gdx.app.log("HELL", "DISPOSE CALLED CONDUCTOR")
        KtxAsync.launch {
            if (Platform.isAndroidSAF && file.exists())
                file.delete()
        }
    }

    fun think(delta: Float) {

    }
}