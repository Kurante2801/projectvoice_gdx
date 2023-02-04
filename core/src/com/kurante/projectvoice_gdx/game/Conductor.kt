package com.kurante.projectvoice_gdx.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Disposable
import games.rednblack.miniaudio.MASound
import games.rednblack.miniaudio.loader.MASoundLoaderParameters
import ktx.app.Platform

class Conductor(
    private val assetManager: AssetManager,
    private val handle: FileHandle,
) : Disposable {

    lateinit var sound: MASound
    var loaded = false

    init {
        assetManager.load(
            handle.path(),
            MASound::class.java,
            MASoundLoaderParameters().apply {
                external = true
            }
        )
        /*KtxAsync.launch(newSingleThreadAsyncContext()) {
            sound = assetStorage.load(handle.path())
            loaded = true
        }*/
    }

    override fun dispose() {
        if(Platform.isAndroid && loaded) {
            // ??
        }
    }

    fun think(delta: Float) {
        if(assetManager.update()) {
            sound = assetManager.get(handle.path())
            loaded = true
        }
    }
}