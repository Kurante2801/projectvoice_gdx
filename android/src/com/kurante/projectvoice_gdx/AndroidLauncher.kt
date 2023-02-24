package com.kurante.projectvoice_gdx

import android.content.Context
import android.content.Intent
import android.os.Bundle
import barsoosayque.libgdxoboe.OboeAudio
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidComponentApplication
import kurante.gdxscopedstorage.ScopedStorageHandler
import kurante.gdxscopedstorage.util.PermissionsLauncher

class AndroidLauncher : AndroidComponentApplication() {
    lateinit var storageHandler: ScopedStorageHandler
    private val permsLauncher = PermissionsLauncher(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storageHandler = ScopedStorageHandler(this, permsLauncher)

        initialize(ProjectVoice(AndroidCallbacks(this)), AndroidApplicationConfiguration().apply {
            useCompass = false
            useGyroscope = false
            useAccelerometer = false
            useImmersiveMode = true
            useRotationVectorSensor = false
            numSamples = 2
        })
    }

    override fun createAudio(context: Context, config: AndroidApplicationConfiguration) =
        OboeAudio(context.assets)

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        storageHandler.onActivityResult(requestCode, resultCode, data)
    }
}