package com.kurante.projectvoice_gdx.level

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.GdxRuntimeException
import com.kurante.projectvoice_gdx.storage.StorageManager.storageHandler

object LevelManager {
    val levels = mutableListOf<Level>()
    var loaded = false

    fun loadLevels(tree: FileHandle) {
        if(!storageHandler.canRead(tree))
            throw GdxRuntimeException("Cannot read tree: $tree")

        loaded = false
        levels.clear()

        for (directory in tree.list()) {
            if (!directory.isDirectory) {
                Gdx.app.log("LevelManager", "Skipping ${directory.name()} (not a directory)")
                continue
            }

            val config = directory.child("songconfig.txt")
            if (!config.exists()) {
                Gdx.app.log("LevelManager", "Skipping ${directory.name()} (found no songconfig.txt)")
                continue
            }

            try {
                levels.add(Level.fromSongConfig(directory))
            } catch (e: Exception) {
                Gdx.app.error("LevelManager", "Skipping ${directory.name()} (failed to load)", e)
            }
        }

        loaded = true
    }
}