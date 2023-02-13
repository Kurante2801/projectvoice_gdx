package com.kurante.projectvoice_gdx.game

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Disposable
import com.kurante.projectvoice_gdx.storage.StorageManager
import com.kurante.projectvoice_gdx.util.extensions.isAndroidSAF
import com.kurante.projectvoice_gdx.util.extensions.toMillis
import games.rednblack.miniaudio.MASound
import games.rednblack.miniaudio.loader.MASoundLoaderParameters
import kotlinx.coroutines.launch
import ktx.app.Platform
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync

class Conductor(
    private val assetStorage: AssetStorage, // Must be absolute for MiniAudio to load!!
    private val handle: FileHandle,
    val sound: MASound,
) : Disposable {
    companion object {
        suspend fun load(assetStorage: AssetStorage, handle: FileHandle): Conductor {
            val file = if (Platform.isAndroidSAF) StorageManager.copyToCache(handle) else handle
            val sound = assetStorage.load(file.path(), MASoundLoaderParameters().apply { external = true })
            val conductor = Conductor(assetStorage, file, sound)
            conductor.maxTime = sound.length.toMillis()
            conductor.shouldDelete = Platform.isAndroidSAF
            return conductor
        }
    }

    var shouldDelete = false
    var begunPlaying = false
    var paused = true

    // MILLISECONDS
    var minTime = 0
    var maxTime = 10000
    var time = 0
        private set


    init {
        minTime = minTime.coerceAtMost(0)
        time = minTime
    }

    override fun dispose() {
        KtxAsync.launch {
            assetStorage.unload<MASound>(handle.path())
            if (shouldDelete && handle.exists())
                handle.delete()
        }
    }

    fun act(delta: Float) {
        if (paused) {
            if (sound.isPlaying) sound.pause()
            return
        }

        if (!begunPlaying) {
            if (time >= 0) {
                sound.play()
                begunPlaying = true
            } else
                time += delta.toMillis()
            return
        }

        /*if (time >= maxTime) {
            if (sound.isPlaying) sound.pause()
            return
        }*/

        if (!sound.isPlaying) sound.play()
        time = (sound.cursorPosition).toMillis()
    }
}