package com.kurante.projectvoice_gdx

import android.media.MediaMetadataRetriever
import barsoosayque.libgdxoboe.OboeMusic
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.files.FileHandle
import com.kurante.projectvoice_gdx.game.Conductor
import com.kurante.projectvoice_gdx.util.extensions.toMillis
import com.kurante.projectvoice_gdx.util.extensions.toSeconds
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import kotlin.math.min

class OboeConductor(
    private val assetStorage: AssetStorage,
    private val handle: FileHandle,
    meta: MediaMetadataRetriever,
    private val music: OboeMusic,
    private val shouldDelete: Boolean = false,
) : Conductor() {
    override val duration: Int
    private var audioEnded = false

    init {
        duration = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toInt()
        maxTime = duration
        minTime = minTime.coerceAtMost(0)
        time = minTime
        music.isLooping = false

        music.setOnCompletionListener {
            audioEnded = true
        }
    }

    override fun dispose() {
        KtxAsync.launch {
            assetStorage.unload<Music>(handle.path())
            if (shouldDelete && handle.exists())
                handle.delete()
        }
    }

    var lastReport: Float = 0f
    override fun act(delta: Float) {
        if (time >= maxTime) {
            if (music.isPlaying) music.stop()
            return
        }

        if (paused) {
            if (music.isPlaying) music.pause()
            return
        }

        // Support starting the chart before audio starts
        // and ending the chart after the audio ends
        if (time < 0 || time > duration || audioEnded)
            time += delta.toMillis()
        else {
            if (!music.isPlaying)
                music.play()
            // music.position may return the same value, so we interpolate until we get a new value
            if (lastReport == music.position)
                time += delta.toMillis()
            else {
                lastReport = music.position
                time = lastReport.toMillis()
            }
        }
    }

    override fun restart() {
        time = minTime
        music.stop()
        audioEnded = false
    }
}