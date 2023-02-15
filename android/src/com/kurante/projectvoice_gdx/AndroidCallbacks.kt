package com.kurante.projectvoice_gdx

import android.media.MediaMetadataRetriever
import android.os.Build
import barsoosayque.libgdxoboe.OboeMusic
import com.badlogic.gdx.Files
import com.badlogic.gdx.Files.FileType
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.files.FileHandle
import com.kurante.projectvoice_gdx.game.Conductor
import com.kurante.projectvoice_gdx.storage.*
import com.kurante.projectvoice_gdx.util.extensions.absoluteFromLocal
import com.kurante.projectvoice_gdx.util.extensions.copyToCache
import com.kurante.projectvoice_gdx.util.extensions.isAndroidSAF
import ktx.app.Platform

class AndroidCallbacks(private val launcher: AndroidLauncher) : NativeCallbacks() {
    override fun create(game: ProjectVoice) {
    }

    override fun dispose(game: ProjectVoice) {
    }

    override fun pause(game: ProjectVoice) {
    }

    override fun resume(game: ProjectVoice) {
    }

    override suspend fun loadConductor(game: ProjectVoice, handle: FileHandle): Conductor {
        val file = if (Platform.isAndroidSAF) handle.copyToCache() else handle
        // Used to determine song length
        val meta = MediaMetadataRetriever().apply {
            if (file.type() == FileType.Local)
                setDataSource(file.absoluteFromLocal().path())
            else
                setDataSource(file.path())
        }

        return OboeConductor(
            assetStorage = game.assetStorage,
            handle = file,
            meta = meta,
            music = game.assetStorage.load<Music>(file.path()) as OboeMusic,
            shouldDelete = Platform.isAndroidSAF
        )
    }

    override fun getStorageHandler(): StorageHandler {
        // Storage Access Framework (SAF) is required on Android 11+
        // however it's SUPER slow, taking up to 19 seconds to load 27 levels on old devices...
        // so we just don't use it when on android 10 or below
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
            AndroidLegacyStorageHandler(launcher)
        else
            AndroidStorageHandler(launcher)
    }

}