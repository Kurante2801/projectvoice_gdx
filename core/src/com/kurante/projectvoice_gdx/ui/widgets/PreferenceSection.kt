package com.kurante.projectvoice_gdx.ui.widgets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.kurante.projectvoice_gdx.storage.StorageManager.randomString
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import com.kurante.projectvoice_gdx.util.extensions.setLocalizedText
import ktx.scene2d.*
import ktx.scene2d.Scene2DSkin.defaultSkin

open class PreferenceSection : Table(), KTable {
    fun initialize(
        titleKey: String,
        subtextKey: String,
        contentInit: (KContainer<Actor>, Cell<*>) -> Unit
    ) {
        this.addActor(table {
            table {
                it.prefWidth(500f.scaledUi())
                label(titleKey) {
                    it.growX()
                    wrap = true

                    style = LabelStyle(style).apply {
                        font = defaultSkin.getFont("bold")
                    }
                    setLocalizedText(titleKey)
                    setAlignment(Align.left)

                }
                defaults().row()
                label(subtextKey) {
                    it.growX()
                    color = Color(0.8f, 0.8f, 0.8f, 1f)
                    wrap = true
                    setLocalizedText(subtextKey)
                    setAlignment(Align.left)
                }
            }
        })

        container {
            align(Align.left)
            it.growX()
            contentInit(this, it)
        }
    }
}

class SelectionPreferenceSection(
    titleKey: String,
    subtextKey: String,
    init: (@Scene2dDsl PVSelectBox) -> Unit
) : PreferenceSection() {
    init {
        initialize(titleKey, subtextKey) { container, _ ->
            container.pvSelectBox {
                init(this)
            }
        }
    }
}