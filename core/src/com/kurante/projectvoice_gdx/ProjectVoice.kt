package com.kurante.projectvoice_gdx

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
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
import com.kurante.projectvoice_gdx.ui.GameScreen
import com.kurante.projectvoice_gdx.ui.screens.GameplayScreen
import com.kurante.projectvoice_gdx.ui.screens.HomeScreen
import com.kurante.projectvoice_gdx.ui.screens.InitializationScreen
import com.kurante.projectvoice_gdx.ui.screens.StorageScreen
import com.kurante.projectvoice_gdx.util.ChadFontData
import com.kurante.projectvoice_gdx.util.UserInterface.BACKGROUND_COLOR
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.graphics.use
import ktx.scene2d.Scene2DSkin


class ProjectVoice : KtxGame<KtxScreen>() {
    companion object {
        fun getPreferences(): Preferences = Gdx.app.getPreferences("com.kurante.projectvoice_gdx")
    }

    private lateinit var batch: SpriteBatch
    lateinit var assetStorage: AssetStorage
    private val packer = PixmapPacker(2048, 2048, Pixmap.Format.RGBA8888, 2, false)
    private val generators = mutableListOf<FreeTypeFontGenerator>()

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        batch = SpriteBatch()

        Scene2DSkin.defaultSkin = Skin().apply {
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
                genMipMaps = true
                packer = this@ProjectVoice.packer
            }

            // JP Fonts
            val regularJP = generateFont("skin/notosansjp_regular.otf", param)
            val boldJP = generateFont("skin/notosansjp_medium.otf", param)

            // Rubik Fonts
            val regular = generateFont("skin/rubik_regular.ttf", param)
            val bold = generateFont("skin/rubik_semibold.ttf", param)

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

        addScreen(InitializationScreen(this))
        addScreen(StorageScreen(this))
        addScreen(HomeScreen(this))
        addScreen(GameplayScreen(this))
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
    }

    override fun dispose() {
        super.dispose()
        packer.dispose()

        for (generator in generators)
            generator.dispose()
    }

    inline fun <reified Type : GameScreen> changeScreen() = changeScreen(Type::class.java)

    fun <Type : GameScreen> changeScreen(type: Class<Type>) {
        val current = currentScreen as? GameScreen
            ?: return setScreen(type)

        val newScreen = getScreen(type) as? GameScreen
            ?: return setScreen(type)

        current.stage.addAction(Actions.sequence(
            GameScreen.FadeAction(current.opacity, 0f, current),
            Actions.run {
                current.hide()

                newScreen.opacity = 0f
                newScreen.resize(Gdx.graphics.width, Gdx.graphics.height)
                newScreen.show()
                newScreen.stage.addAction(GameScreen.FadeAction(0f, 1f, newScreen))

                currentScreen = newScreen
            }
        ))
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