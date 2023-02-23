package com.kurante.projectvoice_gdx.game.notes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.kurante.projectvoice_gdx.PlayerPreferences
import com.kurante.projectvoice_gdx.game.GameState
import com.kurante.projectvoice_gdx.game.GameplayLogic
import com.kurante.projectvoice_gdx.game.Modifier
import com.kurante.projectvoice_gdx.game.Note
import com.kurante.projectvoice_gdx.util.UserInterface.scaledStageX
import com.kurante.projectvoice_gdx.util.extensions.set

class SwipeNoteBehavior(
    private val prefs: PlayerPreferences,
    atlas: TextureAtlas,
    data: Note,
    state: GameState,
    private val modifiers: HashSet<Modifier>,
) : NoteBehavior(prefs, atlas, data, state, modifiers) {
    override val background: TextureRegion = atlas.findRegion("swipe_back")
    override val foreground: TextureRegion = atlas.findRegion("swipe_fore")

    override val isAuto: Boolean
        get() = modifiers.contains(Modifier.AUTO) || modifiers.contains(Modifier.AUTO_SWIPE)

    override fun render(batch: Batch, info: GameplayLogic.TrackInfo, stage: Stage) {
        if (!shouldRender) return

        val width = NOTE_WIDTH.scaledStageX(stage)
        val drawX = info.center - width * 0.5f
        val drawY = y - width * 0.5f

        if (data.data > 0) {
            batch.color = batch.color.set(prefs.noteSwipeRightBackground, alpha)
            batch.draw(background, width + drawX, drawY, -width, width)
            batch.color = batch.color.set(prefs.noteSwipeRightForeground, alpha)
            batch.draw(foreground, width + drawX, drawY, -width, width)
        }
        else {
            batch.color = batch.color.set(prefs.noteSwipeLeftBackground, alpha)
            batch.draw(background, drawX, drawY, width, width)
            batch.color = batch.color.set(prefs.noteSwipeLeftForeground, alpha)
            batch.draw(foreground, drawX, drawY, width, width)
        }
    }
}