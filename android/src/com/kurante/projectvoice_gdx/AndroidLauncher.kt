package com.kurante.projectvoice_gdx

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.documentfile.provider.DocumentFile
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidComponentApplication
import com.badlogic.gdx.files.FileHandle
import com.kurante.projectvoice_gdx.storage.*

class AndroidLauncher : AndroidComponentApplication() {
    private var treeCallback: ((Uri?) -> Unit)? = null
    private val treeLauncher = registerForActivityResult(OpenDocumentTreePersistent()) {
        treeCallback?.invoke(it)
        treeCallback = null

        if(it != null)
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }

    private var permCallback: (() -> Unit)? = null
    private val permLauncher = registerForActivityResult(RequestMultiplePermissions()) {
        for(perm in it) {
            if(!perm.value) {
                permCallback = null
                break
            }
        }

        permCallback?.invoke()
        permCallback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Safe Access Storage is required on Android 11+
        // however it's SUPER slow, taking up to 19 seconds to load 27 levels on old devices...
        // so we just don't use it when on android 10 or below
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
            StorageManager.storageHandler = AndroidLegacyStorageHandler(this)
        else
            StorageManager.storageHandler = AndroidStorageHandler(this)

        initialize(ProjectVoice(), AndroidApplicationConfiguration().apply {
            useCompass = false
            useGyroscope = false
            useAccelerometer = false
            useImmersiveMode = true
            useRotationVectorSensor = false
        })
    }

    fun openDocumentTree(callback: (AndroidFileHandle?) -> Unit) {
        treeCallback = { uri ->
            if(uri != null) {
                val document = DocumentFile.fromTreeUri(context, uri)

                if(document != null)
                    callback.invoke(AndroidFileHandle(context, document))
                else
                    callback(null)
            }
            else
                callback.invoke(null)
        }

        treeLauncher.launch(null)
    }

    private fun requestLegacyStoragePermissions(callback: () -> Unit) {
        if(checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED && checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            callback.invoke()
            return
        }

        permCallback = callback
        permLauncher.launch(arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE))
    }

    fun openDocumentTreeLegacy(callback: (FileHandle?) -> Unit) {
        treeCallback = { uri ->
            if (uri != null) {
                val document = DocumentFile.fromTreeUri(context, uri)

                if (document != null) {
                    val path = RealPathUtil.getRealPath(context, document.uri)
                    val handle = Gdx.files.absolute(path)
                    callback(if(handle.file().canRead()) handle else null)
                } else
                    callback(null)
            } else
                callback.invoke(null)
        }

        requestLegacyStoragePermissions {
            treeLauncher.launch(null)
        }
    }
}