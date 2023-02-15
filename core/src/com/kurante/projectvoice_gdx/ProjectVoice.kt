package com.kurante.projectvoice_gdx

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.Hinting
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.PropertiesUtils
import com.badlogic.gdx.utils.ScreenUtils
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.VisUI.SkinScale
import com.kurante.projectvoice_gdx.storage.StorageFileHandleResolver
import com.kurante.projectvoice_gdx.storage.StorageManager
import com.kurante.projectvoice_gdx.storage.StorageManager.randomString
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.screens.*
import com.kurante.projectvoice_gdx.util.*
import com.kurante.projectvoice_gdx.util.UserInterface.BACKGROUND_COLOR
import com.kurante.projectvoice_gdx.util.extensions.copy
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.graphics.use
import ktx.scene2d.Scene2DSkin.defaultSkin
import java.util.*


class ProjectVoice(
    private val nativeCallbacks: NativeCallbacks
) : KtxGame<KtxScreen>() {
    companion object {
        const val PI = 3.1415927f
        fun getPreferences(): Preferences = Gdx.app.getPreferences("com.kurante.projectvoice_gdx")
    }

    private lateinit var batch: SpriteBatch
    lateinit var assetStorage: AssetStorage
    lateinit var absoluteStorage: AssetStorage
    lateinit var internalStorage: AssetStorage
    private val packer = PixmapPacker(2048, 2048, Pixmap.Format.RGBA8888, 2, false)
    private val generators = mutableListOf<FreeTypeFontGenerator>()

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

        loadSkin()

        KtxAsync.initiate()
        assetStorage = AssetStorage(
            fileResolver = StorageFileHandleResolver()
        )
        internalStorage = AssetStorage(
            fileResolver = InternalFileHandleResolver()
        )


        absoluteStorage = AssetStorage(
            fileResolver = AbsoluteFileHandleResolver()
        )

        PlayerPreferences.locales["en"] = "English"
        PlayerPreferences.locales["es"] = "Español"
        UserInterface.setLocale(PlayerPreferences.locale)

        nativeCallbacks.create(this)

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
        internalStorage.dispose()
        packer.dispose()

        for (generator in generators)
            generator.dispose()

        nativeCallbacks.dispose(this)
    }

    override fun pause() {
        super.pause()
        nativeCallbacks.pause(this)
    }

    override fun resume() {
        super.resume()
        nativeCallbacks.resume(this)
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

    private fun loadSkin() {
        VisUI.load(SkinScale.X2)
        defaultSkin = VisUI.getSkin().apply {
            var param = FreeTypeFontParameter().apply {
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

            // Rubik Fonts
            val regular = generateFont("skin/rubik_regular.ttf", param)
            val bold = generateFont("skin/rubik_semibold.ttf", param)

            // JP Fonts
            param = param.copy {
                characters = randomString(480)
            }
            val regularJP = generateFont("skin/notosansjp_medium.otf", param)
            val boldJP = generateFont("skin/notosansjp_bold.otf", param)
            // Fixes font being offset vertically
            boldJP.data.ascent = 4f
            regularJP.data.ascent = 4f

            fpsFont = generateFont("skin/rubik_semibold.ttf", param.copy {
                size = 22
            })

            // Apply fallbacks
            (regular.data as ChadFontData).fallbackFonts.add(regularJP.data as ChadFontData)
            (bold.data as ChadFontData).fallbackFonts.add(boldJP.data as ChadFontData)

            add("regular", regular)
            add("bold", bold)

            val atlas = Gdx.files.internal("skin/skin.atlas")
            addRegions(TextureAtlas(atlas))
            load(Gdx.files.internal("skin/skin.json"))

            //val sliderTex = TintedNinePatchDrawable(get("sliderbackground", NinePatch::class.java), true)
            val style = SliderStyle().apply {
                background = getDrawable("blank")
                knob = TintedTextureRegionDrawable(get("sliderknob", TextureRegion::class.java), UserInterface.FOREGROUND2_COLOR)
                knobDown = TintedTextureRegionDrawable(get("sliderknob", TextureRegion::class.java), true)
                knobBefore = TintedNinePatchDrawable(get("sliderbackground", NinePatch::class.java), true)
                knobAfter = TintedNinePatchDrawable(get("sliderbackground", NinePatch::class.java), UserInterface.FOREGROUND1_COLOR)
            }

            add("default-horizontal", style)
        }
    }

    private fun generateFont(
        path: String,
        parameter: FreeTypeFontParameter,
    ): BitmapFont {
        val generator = FreeTypeFontGenerator(Gdx.files.internal(path))
        generators.add(generator)
        return generator.generateFont(parameter, ChadFontData(generator))
    }

    suspend fun loadConductor(handle: FileHandle) = nativeCallbacks.loadConductor(this, handle)
}