package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.PixmapPacker
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.game.Conductor
import com.kurante.projectvoice_gdx.game.GameplayLogic
import com.kurante.projectvoice_gdx.game.Legacy
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
import kotlin.math.log

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
                        this@GameplayScreen.parent.changeScreen<HomeScreen>()
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
        val chart = Legacy.parseChart(level, section)

        KtxAsync.launch {
            val packer = PixmapPacker(2048, 2048, Pixmap.Format.RGBA8888, 2, false)
            val conductor = parent.loadConductor(level.file.child(level.musicFilename))

            packer.packToTexture = true

            packer.pack("background", parent.internalStorage.load<Pixmap>("game/track_background.png"))
            packer.pack("line", parent.internalStorage.load<Pixmap>("game/track_line.png"))
            packer.pack("glow", parent.internalStorage.load<Pixmap>("game/track_glow.png"))
            packer.pack("white", parent.internalStorage.load<Pixmap>("game/white.png"))

            val trackAtlas = packer.generateTextureAtlas(TextureFilter.Nearest, TextureFilter.Nearest, false)
            logic = GameplayLogic(conductor, chart, trackAtlas)
            packer.dispose()

            initialized = true
        }
    }
}