package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.PixmapPacker
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.game.GameState
import com.kurante.projectvoice_gdx.game.GameplayLogic
import com.kurante.projectvoice_gdx.util.LegacyParser
import com.kurante.projectvoice_gdx.level.ChartSection
import com.kurante.projectvoice_gdx.level.Level
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.widgets.PVImageTextButton
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import com.kurante.projectvoice_gdx.util.extensions.crop
import com.kurante.projectvoice_gdx.util.extensions.parseNinePatch
import com.kurante.projectvoice_gdx.util.extensions.pvImageTextButton
import com.kurante.projectvoice_gdx.util.extensions.toSeconds
import kotlinx.coroutines.launch
import ktx.actors.onChange
import ktx.assets.disposeSafely
import ktx.async.KtxAsync
import ktx.scene2d.*
import java.text.DecimalFormat

class GameplayScreen(parent: ProjectVoice) : GameScreen(parent) {
    lateinit var level: Level
    lateinit var logic: GameplayLogic
    private var initialized = false

    private lateinit var pauseButton: PVImageTextButton
    private lateinit var exitButton: PVImageTextButton
    private lateinit var statusText: Label

    private val formatter = DecimalFormat("0.##")


    override fun populate() {
        table = scene2d.table {
            setFillParent(true)

            horizontalGroup {
                align(Align.left)
                space(28f.scaledUi())
                it.growX()
                it.pad(28f.scaledUi())

                pauseButton = pvImageTextButton("LOADING") {
                    onChange {
                        if (initialized)
                            logic.setPaused(!logic.getPaused())
                    }
                }

                exitButton = pvImageTextButton("LEAVE") {
                    onChange {
                        exitGame()
                    }
                }

                statusText = label("")
            }
            defaults().row()
            container {
                it.grow()
                it.pad(0f, 28f.scaledUi(), 28f.scaledUi(), 28f.scaledUi())
            }
        }

        stage.addActor(table)
    }

    override fun render(delta: Float) {
        if (initialized) {
            logic.act(delta)
            logic.render(stage.batch)

            pauseButton.text = formatter.format(logic.time/*.toSeconds()*/)
            exitButton.text = formatter.format(logic.maxTime.toSeconds())
        }

        super.render(delta)
    }

    override fun hide() {
        if (this::logic.isInitialized)
            logic.disposeSafely()
        super.hide()
    }

    // Lots of loading
    fun initialize(level: Level, section: ChartSection) {
        this.level = level
        val chart = LegacyParser.parseChart(level, section)

        KtxAsync.launch {
            val conductor = game.loadConductor(level.file.child(level.musicFilename))

            var packer = PixmapPacker(512, 2048, Pixmap.Format.RGBA8888, 2, false).apply {
                packToTexture = true
                pack("background", game.internalStorage.load<Pixmap>("game/track_background.png"))
                pack("line", game.internalStorage.load<Pixmap>("game/track_line.png"))
                pack("white", game.internalStorage.load<Pixmap>("game/white.png"))
                pack("active", game.internalStorage.load<Pixmap>("game/track_active.png"))
            }
            val trackAtlas = packer.generateTextureAtlas(TextureFilter.Nearest, TextureFilter.Nearest, false)
            packer.dispose()

            val ninePatch: NinePatch
            val background: Pixmap
            // Load Hold Background as Pixmap so we can pack it
            game.internalStorage.load<Pixmap>("game/notes/diamond/hold_back.9.png").apply {
                ninePatch = parseNinePatch()
                background = crop()
            }

            packer = PixmapPacker(2048, 2048, Pixmap.Format.RGBA8888, 2, false).apply {
                packToTexture = true
                pack("click_back", game.internalStorage.load<Pixmap>("game/notes/diamond/click_back.png"))
                pack("click_fore", game.internalStorage.load<Pixmap>("game/notes/diamond/click_fore.png"))
                pack("hold_back", background)
                pack("slide_back", game.internalStorage.load<Pixmap>("game/notes/diamond/slide_back.png"))
                pack("slide_fore", game.internalStorage.load<Pixmap>("game/notes/diamond/slide_fore.png"))
                pack("swipe_back", game.internalStorage.load<Pixmap>("game/notes/diamond/swipe_back.png"))
                pack("swipe_fore", game.internalStorage.load<Pixmap>("game/notes/diamond/swipe_fore.png"))
                pack("tick_back", game.internalStorage.load<Pixmap>("game/notes/tick_back.png"))
                pack("tick_fore", game.internalStorage.load<Pixmap>("game/notes/tick_fore.png"))
                pack("perfect", game.internalStorage.load<Pixmap>("game/notes/diamond/grade_perfect.png"))
                pack("input", game.internalStorage.load<Pixmap>("game/notes/diamond/grade_input.png"))
            }
            val notesAtlas = packer.generateTextureAtlas(TextureFilter.MipMap, TextureFilter.MipMap, true)
            packer.dispose()

            val glowTexture = game.internalStorage.load<Texture>("game/track_glow.png").apply {
                setFilter(TextureFilter.Linear, TextureFilter.Linear)
            }

            logic = GameplayLogic(
                conductor = conductor,
                chart = chart,
                trackAtlas = trackAtlas,
                notesAtlas = notesAtlas,
                modifiers = game.modifiers,
                prefs = game.prefs,
                state = GameState(level, section, chart),
                glowTexture = glowTexture,
                holdBackground = ninePatch
            )

            game.blurEnabled = true
            initialized = true
        }
    }

    fun exitGame() {
        game.backgroundEnabled = false
        game.changeScreen<HomeScreen>()
    }
}