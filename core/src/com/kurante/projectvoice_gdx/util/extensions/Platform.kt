package com.kurante.projectvoice_gdx.util.extensions

import com.kurante.projectvoice_gdx.storage.StorageManager
import ktx.app.Platform

@Suppress("unused")
val Platform.isAndroidSAF: Boolean
    get() = StorageManager.storageHandler.isSAF()