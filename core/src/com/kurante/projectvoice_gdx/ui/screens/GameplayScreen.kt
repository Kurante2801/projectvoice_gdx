package com.kurante.projectvoice_gdx.ui.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.PixmapPacker
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.kurante.projectvoice_gdx.ProjectVoice
import com.kurante.projectvoice_gdx.game.Chart
import com.kurante.projectvoice_gdx.game.Gamemode
import com.kurante.projectvoice_gdx.game.GameState
import com.kurante.projectvoice_gdx.game.GameplayLogic
import com.kurante.projectvoice_gdx.level.ChartSection
import com.kurante.projectvoice_gdx.level.Level
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.widgets.PVImageTextButton
import com.kurante.projectvoice_gdx.util.Interpolations
import com.kurante.projectvoice_gdx.util.KActions
import com.kurante.projectvoice_gdx.util.LegacyParser
import com.kurante.projectvoice_gdx.util.UserInterface
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import com.kurante.projectvoice_gdx.util.extensions.*
import kotlinx.coroutines.launch
import ktx.actors.onChange
import ktx.assets.disposeSafely
import ktx.async.KtxAsync
import ktx.scene2d.*
import ktx.scene2d.Scene2DSkin.defaultSkin
import java.text.DecimalFormat

class GameplayScreen(parent: ProjectVoice) : GameScreen(parent) {
    companion object {
        fun loadFonts(game: ProjectVoice, skin: Skin, param: FreeTypeFontParameter) {
            skin.add("combo", game.generateFont("skin/rubik_semibold.ttf", param.copy {
                size = 84
                characters = "0123456789"
            }))

            skin.add("comboText", game.generateFont("skin/rubik_semibold.ttf", param.copy {
                size = 38
                characters = "COMB"
            }))

            skin.add("score", game.generateFont("skin/rubik_semibold.ttf", param.copy {
                size = 62
                characters = "0123456789"
            }))

            skin.add("accuracy", game.generateFont("skin/rubik_semibold.ttf", param.copy {
                size = 48
                characters = "0123456789%.,"
            }))
        }
    }

    lateinit var level: Level
    lateinit var section: ChartSection
    lateinit var chart: Chart
    lateinit var logic: GameplayLogic
    private var initialized = false

    private lateinit var pauseButton: PVImageTextButton
    private lateinit var exitButton: PVImageTextButton

    private lateinit var combo: Label
    private lateinit var comboText: Label
    private lateinit var score: Label
    private lateinit var accuracy: Label
    private lateinit var title: Label
    private lateinit var difficulty: Label
    private lateinit var comboTable: Table
    private lateinit var modsGroup: KHorizontalGroup

    private var scoreValue = 0f
    private var scoreDisplay = 0f

    private val formatter = DecimalFormat("0.##")
    private val scoreFormatter = DecimalFormat("000000")
    private val comboColor = Color(1f, 1f, 1f, 0f)

    override fun populate() {
        val table = scene2d.table {
            setFillParent(true)

            horizontalGroup {
                touchable = Touchable.childrenOnly

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
                        // MiniAudio crashes if we leave before it's loaded
                        if (initialized)
                            exitGame()
                    }
                }
            }

            defaults().row()
            container {
                touchable = Touchable.disabled
                it.grow()
                it.pad(0f, 28f.scaledUi(), 28f.scaledUi(), 28f.scaledUi())
            }
            defaults().row()
            modsGroup = horizontalGroup {
                touchable = Touchable.disabled

                align(Align.right)
                space(28f.scaledUi())
                it.growX()
                it.pad(28f.scaledUi())
            }
        }

        val infoTable = scene2d.table {
            setFillParent(true)
            touchable = Touchable.disabled

            container { it.grow() }

            table {
                align(Align.topRight)
                it.width(350f.scaledUi())
                it.growY()
                // Texts' top paddings are less than 28f because texts have an empty gap on top of them
                it.pad(19f.scaledUi(), 0f, 0f, 28f.scaledUi())

                score = label("1000000") {
                    it.growX()
                    style = style.withFont(defaultSkin.getFont("score"))
                    setAlignment(Align.right)
                }
                defaults().row()
                accuracy = label("100%") {
                    it.growX()
                    style = style.withFont(defaultSkin.getFont("accuracy"))
                    setAlignment(Align.right)
                }
                defaults().row()
                title = label("Title") {
                    it.growX()
                    style = style.withFont(defaultSkin.getFont("bold"))
                    wrap = true
                    setAlignment(Align.right)
                }
                defaults().row()
                difficulty = label("Extra 16") {
                    it.growX()
                    style = style.withFont(defaultSkin.getFont("bold"))
                    wrap = true
                    setAlignment(Align.right)
                }
            }
        }

        comboTable = scene2d.table {
            setFillParent(true)
            touchable = Touchable.disabled
            combo = label("39") {
                it.pad(16f.scaledUi(), 0f, -8f.scaledUi(), 0f)
                setAlignment(Align.bottom)
                style = style.withFont(defaultSkin.getFont("combo"))
            }
            defaults().row()

            comboText = label("COMBO") {
                setAlignment(Align.top)
                style = style.withFont(defaultSkin.getFont("comboText"))
            }

            defaults().row()
            container { it.grow() }
        }

        addUiAlphaActions(0f, 0f)
        if (initialized) {
            updateLabels()
            addUiAlphaActions(1f, 0.25f)
        }

        tables.add(table)
        stage.addActor(table)

        tables.add(infoTable)
        stage.addActor(infoTable)

        stage.addActor(comboTable)
    }

    override fun render(delta: Float) {
        if (initialized) {
            logic.act(delta)
            logic.render(stage.batch)

            pauseButton.text = formatter.format(logic.time/*.toSeconds()*/)
            exitButton.text = formatter.format(logic.maxTime.toSeconds())

            scoreDisplay = scoreDisplay.lerp(scoreValue, 0.6f)
            score.setText(scoreFormatter.format(scoreDisplay))

            combo.fontScaleX = combo.fontScaleX.coerceIn(1f, 1.25f)
            combo.fontScaleY = combo.fontScaleY.coerceIn(1f, 1.25f)
            comboText.color = comboText.color.set(combo.color)
            comboText.fontScaleX = combo.fontScaleX
            comboText.fontScaleY = combo.fontScaleY
        }

        super.render(delta)
    }

    override fun hide() {
        if (this::logic.isInitialized)
            logic.disposeSafely()
        super.hide()
        addUiAlphaActions(0f, 0f)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        comboTable.pad(Gdx.graphics.safeInsetTop.toFloat(), 0f, 0f, 0f)
    }

    // Lots of loading
    fun initialize(level: Level, section: ChartSection) {
        initialized = false

        this.level = level
        this.section = section
        chart = LegacyParser.parseChart(level, section)
        game.backgroundOpacity = game.prefs.backgroundOpacity

        scoreDisplay = 0f
        scoreValue = 0f

        if (populated)
            updateLabels()

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
                pack("track", game.internalStorage.load<Pixmap>("game/notes/diamond/track.png"))
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
                holdBackground = ninePatch,
                screen = this@GameplayScreen,
                game = game
            )

            if (populated) {
                updateLabels()
                addUiAlphaActions(1f, 0.25f)
            }

            game.blurAlpha = 1f
            initialized = true
        }
    }

    fun exitGame() {
        game.backgroundOpacity = 0f
        game.backgroundEnabled = false
        game.changeScreen<HomeScreen>()
    }

    fun addUiAlphaActions(target: Float, duration: Float) {
        if (game.gamemode == Gamemode.TRACKS && target != 0f)
            return addUiAlphaActions(0f, duration)

        val color = Color(1f, 1f, 1f, target)
        score.clearActions()
        score.addAction(Actions.color(color, duration))
        accuracy.clearActions()
        accuracy.addAction(Actions.color(color, duration))
        title.clearActions()
        title.addAction(Actions.color(color, duration))
        modsGroup.clearActions()
        modsGroup.addAction(Actions.color(color, duration))

        difficulty.clearActions()
        if (this::section.isInitialized)
            difficulty.addAction(Actions.color(section.type.color.withAlpha(target), duration))
        else
            difficulty.addAction(Actions.color(color, duration))

        if (target == 0f) {
            combo.clearActions()
            combo.addAction(Actions.color(color, duration))
        }
    }

    fun updateLabels() {
        title.setText(level.title)
        difficulty.setText("${section.name} ${section.difficulty}")
        score.setText("000000")
        accuracy.setText("100%")

        combo.setText("0")
        combo.clearActions()
        combo.color = combo.color.setAlpha(0f)
        comboText.color = comboText.color.setAlpha(0f)

        scoreDisplay = 0f
        scoreValue = 0f

        modsGroup.clear()
        modsGroup.color = modsGroup.color.setAlpha(0f)
        for (mod in game.modifiers) {
            modsGroup.addActor(TextButton(mod.toString().lowercase().capitalize(), defaultSkin).apply {
                color = UserInterface.mainColor
            })
        }
    }

    fun scoreChanged(state: GameState) {
        scoreValue = state.score.toFloat()

        if (state.accuracy == 100.0)
            accuracy.setText("100%")
        else
            accuracy.setText("${formatter.format(state.accuracy * 100f)}%")

        combo.clearActions()
        if (state.combo > 0) {
            combo.setText(state.combo.toString())
            combo.clearActions()
            combo.addAction(
                Actions.parallel(
                    Actions.color(comboColor.setAlpha(1f), 0.25f),
                    Actions.sequence(
                        KActions.scaleLabelBy(0.25f, 0.25f, 0.0625f, Interpolations.outCubic),
                        KActions.scaleLabelBy(-0.25f, -0.25f, 0.0625f, Interpolations.outCubic),
                    )
                )
            )
        } else
            combo.addAction(Actions.color(comboColor.setAlpha(0f), 0.25f))
    }
}