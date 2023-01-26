package com.kurante.projectvoice_gdx.level

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle

object LevelManager {
    val levels = mutableListOf<Level>()
    var loaded = false

    fun loadLevels(tree: FileHandle) {
        loaded = false
        levels.clear()

        for(directory in tree.list()) {
            if(!directory.isDirectory) {
                Gdx.app.log("com.kurante.projectvoice_gdx", "Skipping ${directory.name()}. Not a directory")
                continue
            }

            val config = directory.child("songconfig.txt")
            if(!config.exists()) {
                Gdx.app.log("com.kurante.projectvoice_gdx", "Skipping ${directory.name()}. Found no songconfig.txt")
                continue
            }

            try {
                levels.add(Level.fromSongConfig(directory))
            } catch (e: Exception) {
                Gdx.app.error("com.kurante.projectvoice_gdx", "Failed loading ${directory.name()}", e)
            }
        }

        loaded = true
    }
}