package com.kurante.projectvoice_gdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Disposable
import com.kurante.projectvoice_gdx.storage.StorageManager
import com.kurante.projectvoice_gdx.util.extensions.isAndroidSAF
import com.kurante.projectvoice_gdx.util.extensions.toMillis
import com.kurante.projectvoice_gdx.util.extensions.toSeconds
import games.rednblack.miniaudio.MASound
import games.rednblack.miniaudio.loader.MASoundLoaderParameters
import kotlinx.coroutines.launch
import ktx.app.Platform
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import kotlin.math.max
import kotlin.math.min

class Conductor(
    private val assetStorage: AssetStorage, // Must be absolute for MiniAudio to load!!
    handle: FileHandle,
    postLoaded: (Conductor?) -> Unit = {},
) : Disposable {
    val file = if(Platform.isAndroidSAF) StorageManager.copyToCache(handle) else handle
    lateinit var sound: MASound

    var loaded = false
    var begunPlaying = false
    var paused = true

    // MILLISECONDS
    var minTime = 0
    var maxTime = 10000
    var time = 0
        private set


    init {
        KtxAsync.launch {
            try {
                sound = assetStorage.load(file.path(), MASoundLoaderParameters().apply { external = true })
                loaded = true

                // We can't seek reliably so we MUST play the sound from start
                minTime = minTime.coerceAtMost(0)
                time = minTime

                postLoaded.invoke(this@Conductor)
            } catch (e: Exception) {
                e.printStackTrace()
                postLoaded.invoke(null)
            }
        }
    }

    override fun dispose() {
        if (Platform.isAndroidSAF && file.exists()) {
            KtxAsync.launch {
                file.delete()
            }
        }
    }

    fun act(delta: Float) {
        if (paused && sound.isPlaying) sound.pause()
        if (!loaded || paused) return

        if (!begunPlaying) {
            if (time >= 0) {
                sound.play()
                begunPlaying = true
            } else
                time += delta.toMillis()
            return
        }

        if (time >= maxTime) {
            if (sound.isPlaying) sound.pause()
            return
        }

        if (!sound.isPlaying) sound.play()
        time = sound.cursorPosition.toMillis()
    }
}