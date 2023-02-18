package com.kurante.projectvoice_gdx

import com.badlogic.gdx.files.FileHandle
import com.kurante.projectvoice_gdx.game.Conductor
import com.kurante.projectvoice_gdx.storage.StorageManager
import com.kurante.projectvoice_gdx.util.extensions.isAndroidSAF
import com.kurante.projectvoice_gdx.util.extensions.toMillis
import games.rednblack.miniaudio.MASound
import games.rednblack.miniaudio.loader.MASoundLoaderParameters
import kotlinx.coroutines.launch
import ktx.app.Platform
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync

class MiniAudioConductor(
    private val assetStorage: AssetStorage,
    private val handle: FileHandle,
    private val sound: MASound,
) : Conductor() {
    override val duration: Int

    init {
        duration = sound.length.toMillis()
        minTime = minTime.coerceAtMost(0)
        maxTime = duration
        time = minTime
        sound.isLooping = false
    }

    override fun dispose() {
        KtxAsync.launch {
            assetStorage.unload<MASound>(handle.path())
        }
    }

    override fun act(delta: Float) {
        if (time >= maxTime) {
            if (sound.isPlaying) sound.stop()
            return
        }

        if (paused) {
            if (sound.isPlaying) sound.pause()
            return
        }

        // Support starting the chart before audio starts
        // and ending the chart after the audio ends
        if (time < 0 || time > duration)
            time += delta.toMillis()
        else {
            if (!sound.isPlaying) sound.play()
            time = sound.cursorPosition.toMillis()
        }
    }

    override fun restart() {
        time = 0
        sound.stop()
    }
}