package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.kurante.projectvoice_gdx.util.UserInterface

fun Actor.setMainColor(duration: Float = 0.25f) {
    this.color = UserInterface.mainColor
    UserInterface.mainColorEvent += { this.addAction(Actions.color(it, duration)) }
}