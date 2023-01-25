package com.kurante.projectvoice_gdx

import android.net.Uri
import android.os.Bundle
import androidx.documentfile.provider.DocumentFile
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidComponentApplication
import com.kurante.fat.OpenDocumentTreePersistent
import com.kurante.projectvoice_gdx.storage.AndroidFileHandler
import com.kurante.projectvoice_gdx.storage.AndroidStorageHandler
import com.kurante.projectvoice_gdx.storage.StorageManager

class AndroidLauncher : AndroidComponentApplication() {
    private var treeCallback: ((Uri?) -> Unit)? = null
    private val treeLauncher = registerForActivityResult(OpenDocumentTreePersistent()) {
        treeCallback?.invoke(it)
        treeCallback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StorageManager.handler = AndroidStorageHandler(this)

        initialize(ProjectVoice(), AndroidApplicationConfiguration().apply {
            useCompass = false
            useGyroscope = false
            useAccelerometer = false
            useImmersiveMode = true
            useRotationVectorSensor = false
        })
    }

    fun openDocumentTree(callback: (AndroidFileHandler?) -> Unit) {
        treeCallback = { uri ->
            if(uri != null) {
                val document = DocumentFile.fromSingleUri(context, uri)
                if(document != null)
                    callback.invoke(AndroidFileHandler(document, context))
                else
                    callback(null)
            }
            else
                callback.invoke(null)
        }

        treeLauncher.launch(null)
    }
}