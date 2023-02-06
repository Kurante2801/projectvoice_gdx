package com.kurante.projectvoice_gdx.game

import com.badlogic.gdx.files.FileHandle
import games.rednblack.miniaudio.MASound
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext

class Conductor(
    private val assetStorage: AssetStorage,
    private val handle: FileHandle,
) {
    lateinit var sound: MASound
    var loaded = false

    init {
        KtxAsync.launch(newSingleThreadAsyncContext()) {
            sound = assetStorage.load(handle.path())
            loaded = true
        }
    }
}