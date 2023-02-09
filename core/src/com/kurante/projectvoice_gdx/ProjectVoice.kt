package com.kurante.projectvoice_gdx

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.PixmapPacker
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.Hinting
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ScreenUtils
import com.kurante.projectvoice_gdx.storage.StorageFileHandleResolver
import com.kurante.projectvoice_gdx.storage.StorageManager
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.screens.*
import com.kurante.projectvoice_gdx.util.ChadFontData
import com.kurante.projectvoice_gdx.util.ComposedSkinFontless
import com.kurante.projectvoice_gdx.util.UserInterface
import com.kurante.projectvoice_gdx.util.UserInterface.BACKGROUND_COLOR
import com.kurante.projectvoice_gdx.util.UserInterface.scaledUi
import com.kurante.projectvoice_gdx.util.WidthViewport.Companion.REFERENCE_WIDTH
import games.rednblack.miniaudio.MASound
import games.rednblack.miniaudio.MiniAudio
import games.rednblack.miniaudio.loader.MASoundLoader
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.graphics.use
import ktx.scene2d.Scene2DSkin
import java.lang.reflect.Type
import java.util.*


class ProjectVoice(
    private val nativeCallback: (ProjectVoice) -> Unit = {}
) : KtxGame<KtxScreen>() {
    companion object {
        fun getPreferences(): Preferences = Gdx.app.getPreferences("com.kurante.projectvoice_gdx")
    }

    private lateinit var batch: SpriteBatch
    lateinit var assetStorage: AssetStorage
    lateinit var absoluteStorage: AssetStorage
    private val packer = PixmapPacker(2048, 2048, Pixmap.Format.RGBA8888, 2, false)
    private val generators = mutableListOf<FreeTypeFontGenerator>()

    lateinit var miniAudio: MiniAudio

    private val screenHistory = ArrayDeque<GameScreen>()

    private lateinit var fpsFont: BitmapFont

    override fun create() {
        // Create cache
        StorageManager.cachePath = Gdx.files.localStoragePath + "cache/"
        StorageManager.cache = Gdx.files.absolute(StorageManager.cachePath)

        // Android does NOT call dispose() when closing the app...
        // so we must manually remove the cache here
        StorageManager.cache.mkdirs()
        for (file in StorageManager.cache.list())
            file.delete()

        Gdx.app.logLevel = Application.LOG_DEBUG
        batch = SpriteBatch()

        Scene2DSkin.defaultSkin = ComposedSkinFontless().apply {
            val param = FreeTypeFontParameter().apply {
                size = 38
                hinting = Hinting.AutoFull
                gamma = 1f
                shadowOffsetX = 2
                shadowOffsetY = 2
                shadowColor = Color(0f, 0f, 0f, 0.29f)
                minFilter = Texture.TextureFilter.Linear
                magFilter = Texture.TextureFilter.Linear
                color = Color.WHITE
                renderCount = 1
                incremental = true
                packer = this@ProjectVoice.packer
            }

            // JP Fonts
            val regularJP = generateFont("skin/notosansjp_regular.otf", param)
            val boldJP = generateFont("skin/notosansjp_medium.otf", param)

            // Rubik Fonts
            val regular = generateFont("skin/rubik_regular.ttf", param)
            val bold = generateFont("skin/rubik_semibold.ttf", param)

            param.size = 22
            fpsFont = generateFont("skin/rubik_semibold.ttf", param)

            // Apply fallbacks
            (regular.data as ChadFontData).fallbackFonts.add(regularJP.data as ChadFontData)
            (bold.data as ChadFontData).fallbackFonts.add(boldJP.data as ChadFontData)

            add("regular", regular)
            add("bold", bold)

            val atlas = Gdx.files.internal("skin/skin.atlas")
            addRegions(TextureAtlas(atlas))
            load(Gdx.files.internal("skin/skin.json"))
        }

        KtxAsync.initiate()
        assetStorage = AssetStorage(
            fileResolver = StorageFileHandleResolver()
        )

        miniAudio = MiniAudio()

        absoluteStorage = AssetStorage(
            fileResolver = AbsoluteFileHandleResolver()
        ).apply {
            setLoader<MASound> { MASoundLoader(miniAudio, fileResolver) }
        }

        UserInterface.setLocale("es") // TODO: Preferences Locale

        nativeCallback.invoke(this)

        addScreen(InitializationScreen(this))
        addScreen(StorageScreen(this))
        addScreen(HomeScreen(this))
        addScreen(GameplayScreen(this))
        addScreen(PreferencesScreen(this))

        setScreen<InitializationScreen>()
    }

    override fun render() {
        ScreenUtils.clear(BACKGROUND_COLOR)

        val screen = currentScreen as? GameScreen
        // Used when rendering fade in/out
        if (screen != null && screen.opacity < 1f) {
            screen.renderToBuffer(Gdx.graphics.deltaTime)

            batch.use {
                // Uncomment below if rendering anything else
                // it.color = Color.WHITE

                it.color = Color(1f, 1f, 1f, screen.opacity)
                it.draw(
                    screen.buffer.colorBufferTexture,
                    0f, 0f,
                    screen.buffer.width.toFloat(),
                    screen.buffer.height.toFloat(),
                    0f, 0f, 1f, 1f
                )
            }
        } else
            currentScreen.render(Gdx.graphics.deltaTime)

        batch.use {
            fpsFont.draw(
                batch, "FPS: ${Gdx.graphics.framesPerSecond}",
                Gdx.graphics.safeInsetLeft + 12f, 36f
            )
        }
    }

    override fun dispose() {
        super.dispose()
        assetStorage.dispose()
        absoluteStorage.dispose()
        packer.dispose()

        for (generator in generators)
            generator.dispose()

        miniAudio.dispose()
    }

    override fun pause() {
        miniAudio.stopEngine()
        super.pause()
    }

    override fun resume() {
        super.resume()
        miniAudio.startEngine()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        batch = SpriteBatch()
    }

    inline fun <reified Type : GameScreen> changeScreen(addToHistory: Boolean = true) {
        changeScreen(Type::class.java, addToHistory)
    }

    fun <Type : GameScreen> changeScreen(type: Class<Type>, addToHistory: Boolean = true) {
        changeScreen(getScreen(type), addToHistory)
    }

    fun changeScreen(newScreen: GameScreen, addToHistory: Boolean = true) {
        val current = currentScreen as? GameScreen
        Gdx.input.inputProcessor = null

        if (current == null) {
            currentScreen.hide()
            currentScreen = newScreen
            currentScreen.resize(Gdx.graphics.width, Gdx.graphics.height)
            currentScreen.show()
            return
        }

        if (addToHistory)
            screenHistory.add(newScreen)

        current.stage.addAction(Actions.sequence(
            GameScreen.FadeAction(current.opacity, 0f, current),
            Actions.run {
                current.hide()

                newScreen.opacity = 0f
                newScreen.resize(Gdx.graphics.width, Gdx.graphics.height)
                newScreen.show()
                newScreen.stage.addAction(GameScreen.FadeAction(0f, 1f, newScreen))
                Gdx.input.inputProcessor = newScreen.stage

                currentScreen = newScreen
            }
        ))
    }

    fun getPreviousScreen(): GameScreen? {
        if (screenHistory.isEmpty()) return null

        val screen = screenHistory.peekLast()
        // Is our current screen in the history stack?
        return if (screen::class == currentScreen::class) {
            if (screenHistory.size >= 2) {
                screenHistory.pollLast()
                screenHistory.peekLast()
            } else
                null
        } else {
            if (!screenHistory.isEmpty())
                screenHistory.peekLast()
            else
                null
        }
    }

    fun tryPreviousScreen(): Boolean {
        val screen = getPreviousScreen()
        if (screen != null) {
            changeScreen(screen, false)
            return true
        }
        return false
    }


    private fun generateFont(
        path: String,
        parameter: FreeTypeFontParameter,
    ): BitmapFont {
        val generator = FreeTypeFontGenerator(Gdx.files.internal(path))
        generators.add(generator)
        return generator.generateFont(parameter, ChadFontData(generator))
    }
}