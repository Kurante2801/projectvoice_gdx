package com.kurante.projectvoice_gdx.util.extensions

import com.badlogic.gdx.graphics.Pixmap

fun Pixmap.crop(
    width: Int,
    height: Int,
    aspectRatio: Float,
    disposeOld: Boolean = true
): Pixmap {
    // TODO: Tomorrow
    /*val pixmap = Pixmap(width, height, this.format)

    if(width / height <= aspectRatio) {
        val h = ceil(width / aspectRatio)
        pixmap.drawPixmap(
            this, 0, 0, this.width, this.height,
            0, floor(h * 0.5f - height * 0.5f), width, h
        )
    } else {
        val w = ceil(width * aspectRatio)
        pixmap.drawPixmap(
            this, 0, 0, this.width, this.height,
            floor(w * 0.5f - width * 0.5f), 0, w, height
        )
    }

    if(disposeOld)
        this.dispose()

    return pixmap*/
    return this
}