package com.kurante.projectvoice_gdx

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
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
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.ScreenUtils
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.VisUI.SkinScale
import com.kurante.projectvoice_gdx.game.Modifier
import com.kurante.projectvoice_gdx.game.particles.CollectionParticle
import com.kurante.projectvoice_gdx.storage.StorageFileHandleResolver
import com.kurante.projectvoice_gdx.storage.StorageManager
import com.kurante.projectvoice_gdx.storage.StorageManager.randomString
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.screens.*
import com.kurante.projectvoice_gdx.util.*
import com.kurante.projectvoice_gdx.util.UserInterface.BACKGROUND_COLOR
import com.kurante.projectvoice_gdx.util.extensions.copy
import com.kurante.projectvoice_gdx.util.extensions.envelopeParent
import de.tomgrill.gdxdialogs.core.GDXDialogs
import de.tomgrill.gdxdialogs.core.GDXDialogsSystem
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
        const val BACKGROUND_FADE = 0.25f
        // I'm tired of endlessly passing references around, so I'm making statics
        var stageWidth = 0f
        var stageHeight = 0f

        val particlePool = object : Pool<CollectionParticle>() {
            override fun newObject(): CollectionParticle =
                CollectionParticle(0f, 0f, null, 0f, 0f, 0f)
        }

        init {
            // Add particles to the pool so that we don't have to create them as often later on
            for (i in 0..50)
                particlePool.free(CollectionParticle(0f, 0f, null, 0f, 0f, 0f))
        }
    }

    private lateinit var batch: SpriteBatch
    lateinit var assetStorage: AssetStorage
    lateinit var absoluteStorage: AssetStorage
    lateinit var internalStorage: AssetStorage
    private val packer = PixmapPacker(2048, 2048, Pixmap.Format.RGBA8888, 2, false)
    private val generators = mutableListOf<FreeTypeFontGenerator>()

    private val screenHistory = ArrayDeque<GameScreen>()

    private lateinit var fpsFont: BitmapFont

    lateinit var dialogs: GDXDialogs
    lateinit var prefs: PlayerPreferences

    val modifiers = hashSetOf<Modifier>()

    private lateinit var blurShader: ShaderProgram
    private var backgroundDrawable: TextureRegionDrawable? = null
    private var backgroundAlpha by AnimatableValues.FloatDelegate("backgroundAlpha", 0f)
    var backgroundEnabled = false
    private var backgroundRatio = 1f

    private var blurBuffer: FrameBuffer? = null
    private val blurTexture: Texture? get() = blurBuffer?.colorBufferTexture
    var blurAlpha by AnimatableValues.FloatDelegate("blurAlpha", 0f)

    var backgroundOpacity by AnimatableValues.FloatDelegate("backgroundOpacity", 0f)

    override fun create() {
        //if (Platform.isDesktop)
            modifiers.add(Modifier.AUTO)

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
        dialogs = GDXDialogsSystem.install()

        loadSkin()

        KtxAsync.initiate()
        assetStorage = AssetStorage(fileResolver = StorageFileHandleResolver(nativeCallbacks.getStorageHandler()))
        internalStorage = AssetStorage(fileResolver = InternalFileHandleResolver())
        absoluteStorage = AssetStorage(fileResolver = AbsoluteFileHandleResolver())

        prefs = PlayerPreferences(this)
        prefs.locales["en"] = "English"
        prefs.locales["es"] = "Español"
        UserInterface.setLocale(prefs.locale)

        blurShader = ShaderProgram(Gdx.files.internal("gaussian.vert"), Gdx.files.internal("gaussian.frag"))

        nativeCallbacks.create(this)
        StorageManager.storageHandler = nativeCallbacks.getStorageHandler()

        addScreen(InitializationScreen(this))
        addScreen(StorageScreen(this))
        addScreen(HomeScreen(this))
        addScreen(GameplayScreen(this))
        addScreen(PreferencesScreen(this))

        setScreen<InitializationScreen>()

        getScreen<InitializationScreen>().stage.apply {
            stageWidth = width
            stageHeight = height
        }
    }

    override fun render() {

        ScreenUtils.clear(BACKGROUND_COLOR)
        val screen = currentScreen as? GameScreen
        val delta = Gdx.graphics.deltaTime

        AnimatableValues.act(delta)

        batch.use {
            if (backgroundDrawable != null && backgroundAlpha > 0f) {
                it.color = it.color.set(backgroundOpacity, backgroundOpacity, backgroundOpacity, backgroundAlpha)
                backgroundDrawable!!.draw(it, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
            }
            if (blurAlpha > 0f && blurTexture != null) {
                it.color = it.color.set(backgroundOpacity, backgroundOpacity, backgroundOpacity, blurAlpha)
                it.draw(blurTexture, 0f, 0f)
            }
        }

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
                safeLeft() + 12f, 36f
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
        updateBackground()
    }

    fun safeLeft(): Int = if (prefs.safeArea) Gdx.graphics.safeInsetLeft else 0
    fun safeRight(): Int = if (prefs.safeArea) Gdx.graphics.safeInsetRight else 0
    fun safeTop(): Int = if (prefs.safeArea) Gdx.graphics.safeInsetTop else 0
    fun safeBottom(): Int = if (prefs.safeArea) Gdx.graphics.safeInsetBottom else 0

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
            currentScreen.show()
            currentScreen.resize(Gdx.graphics.width, Gdx.graphics.height)
            return
        }

        if (addToHistory)
            screenHistory.add(newScreen)

        current.stage.addAction(Actions.sequence(
            GameScreen.FadeAction(current.opacity, 0f, current),
            Actions.run {
                current.hide()

                newScreen.opacity = 0f
                newScreen.show()
                newScreen.resize(Gdx.graphics.width, Gdx.graphics.height)
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
            add("debug", fpsFont)

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

    fun setBackground(texture: Texture?, aspectRatio: Float, blur: Float = 0f) {
        if (texture != null)
            backgroundDrawable = TextureRegionDrawable(texture.envelopeParent(
                Gdx.graphics.width.toFloat() / Gdx.graphics.height, aspectRatio)
            )

        blurBuffer?.dispose()
        blurBuffer = null
        backgroundRatio = aspectRatio

        if (texture == null) {
            backgroundEnabled = false
            backgroundAlpha = 0f
            blurAlpha = 0f
            return
        }

        backgroundEnabled = true
        // Blur texture to a FrameBuffer (so we only blur it once, and not every draw call)
        val width = Gdx.graphics.width; val height = Gdx.graphics.height
        val blurred = FrameBuffer(Pixmap.Format.RGBA8888, width, height, false)
        val blurA = FrameBuffer(Pixmap.Format.RGBA8888, width, height, false)
        val blurB = FrameBuffer(Pixmap.Format.RGBA8888, width, height, false)
        val batch = SpriteBatch()

        blurA.use {
            ScreenUtils.clear(0f, 0f, 0f, 1f)
            batch.use { backgroundDrawable!!.draw(it, 0f, 0f, width.toFloat(), height.toFloat()) }
        }

        blurB.use { batch.use {
            it.shader = blurShader
            blurShader.setUniformf("dir", 1f, 0f)
            blurShader.setUniformf("resolution", width.toFloat())
            blurShader.setUniformf("radius", prefs.backgroundBlur)
            it.draw(blurA.colorBufferTexture, 0f, 0f)
        } }

        blurred.use { batch.use {
            blurShader.setUniformf("dir", 0f, 1f)
            it.draw(blurB.colorBufferTexture, 0f, 0f, width.toFloat(), height.toFloat(), 0f, 0f, 1f, 1f)
        } }

        blurBuffer = blurred
        backgroundAlpha = 1f
        blurAlpha = blur

        blurA.dispose()
        blurB.dispose()
        batch.dispose()
    }

    fun updateBackground() {
        if (backgroundDrawable != null && backgroundEnabled)
            setBackground(backgroundDrawable!!.region.texture, backgroundRatio, AnimatableValues["blurAlpha"]!!.target)
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