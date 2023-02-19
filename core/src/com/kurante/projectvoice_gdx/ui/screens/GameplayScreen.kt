package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.PixmapPacker
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.game.GameplayLogic
import com.kurante.projectvoice_gdx.game.LegacyParser
import com.kurante.projectvoice_gdx.level.ChartSection
import com.kurante.projectvoice_gdx.level.Level
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.widgets.PVImageTextButton
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
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
                        this@GameplayScreen.game.changeScreen<HomeScreen>()
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
            logic.render(stage, stage.batch as SpriteBatch)

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

    fun initialize(level: Level, section: ChartSection) {
        this.level = level
        val chart = LegacyParser.parseChart(level, section)

        KtxAsync.launch {
            val conductor = game.loadConductor(level.file.child(level.musicFilename))

            var packer = PixmapPacker(512, 2048, Pixmap.Format.RGBA8888, 2, false).apply {
                packToTexture = true
                pack("background", game.internalStorage.load<Pixmap>("game/track_background.png"))
                pack("line", game.internalStorage.load<Pixmap>("game/track_line.png"))
                pack("glow", game.internalStorage.load<Pixmap>("game/track_glow.png"))
                pack("white", game.internalStorage.load<Pixmap>("game/white.png"))
                pack("active", game.internalStorage.load<Pixmap>("game/track_active.png"))
            }
            val trackAtlas = packer.generateTextureAtlas(TextureFilter.Nearest, TextureFilter.Nearest, false)
            packer.dispose()

            packer = PixmapPacker(2048, 2048, Pixmap.Format.RGBA8888, 2, false).apply {
                packToTexture = true
                pack("click_back", game.internalStorage.load<Pixmap>("game/notes/diamond/click_back.png"))
                pack("click_fore", game.internalStorage.load<Pixmap>("game/notes/diamond/click_fore.png"))
            }
            val notesAtlas = packer.generateTextureAtlas(TextureFilter.MipMap, TextureFilter.MipMap, true)
            packer.dispose()

            logic = GameplayLogic(conductor, chart, trackAtlas, notesAtlas, game.modifiers, game.prefs)
            initialized = true
        }
    }
}