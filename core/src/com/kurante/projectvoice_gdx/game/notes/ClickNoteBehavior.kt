package com.kurante.projectvoice_gdx.game.notes

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.kurante.projectvoice_gdx.PlayerPreferences
import com.kurante.projectvoice_gdx.game.GameState
import com.kurante.projectvoice_gdx.game.GameplayLogic
import com.kurante.projectvoice_gdx.game.Modifier
import com.kurante.projectvoice_gdx.game.Note

class ClickNoteBehavior(
    prefs: PlayerPreferences,
    atlas: TextureAtlas,
    data: Note,
    state: GameState,
    modifiers: HashSet<Modifier>,
    logic: GameplayLogic,
) : NoteBehavior(prefs, atlas, data, state, modifiers, logic)