package com.kurante.projectvoice_gdx.game.notes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.kurante.projectvoice_gdx.PlayerPreferences
import com.kurante.projectvoice_gdx.game.GameState
import com.kurante.projectvoice_gdx.game.GameplayLogic
import com.kurante.projectvoice_gdx.game.Modifier
import com.kurante.projectvoice_gdx.game.Note

class SlideNoteBehavior(
    private val prefs: PlayerPreferences,
    atlas: TextureAtlas,
    data: Note,
    state: GameState,
    private val modifiers: HashSet<Modifier>,
    logic: GameplayLogic,
) : NoteBehavior(prefs, atlas, data, state, modifiers, logic) {
    override val background: TextureRegion = atlas.findRegion("slide_back")
    override val foreground: TextureRegion = atlas.findRegion("slide_fore")

    override val isAuto: Boolean
        get() = modifiers.contains(Modifier.AUTO) || modifiers.contains(Modifier.AUTO_SLIDE)

    override val backColor: Color
        get() = prefs.noteSlideBackground
    override val foreColor: Color
        get() = prefs.noteSlideForeground
}