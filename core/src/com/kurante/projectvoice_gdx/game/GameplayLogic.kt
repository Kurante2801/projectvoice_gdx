package com.kurante.projectvoice_gdx.game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils.*
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.kurante.projectvoice_gdx.util.BakedAnimationCurve
import com.kurante.projectvoice_gdx.util.UserInterface.scaledStageX
import com.kurante.projectvoice_gdx.util.UserInterface.scaledStageY
import com.kurante.projectvoice_gdx.util.extensions.mapRange
import com.kurante.projectvoice_gdx.util.extensions.toMillis
import ktx.graphics.use
import java.lang.Integer.max

class GameplayLogic(
    private val conductor: Conductor,
    chart: Chart,
    private val trackAtlas: TextureAtlas,
) : Disposable {
    private data class DrawCall(
        var center: Int = 240,
        var width: Float = 100f,
        var scaleY: Float = 1f,
        var color: Color = Color(1f, 1f, 1f, 1f),
        var shouldDraw: Boolean = false,
        var animating: Boolean = false,
    )

    companion object {
        // Track's line is larger than the screen and is centered at the judgement line
        // This is so that it looks centered when the tracks spawn and despawn with animation
        const val LINE_POS_MULTIPLIER = 0.16666666f
        const val LINE_HEIGHT_MULTIPLIER = 1.7083f

        // You can copy-paste the Unity lines into a Unity AnimationCurve field to see exactly what the curve is

        // UnityEditor.AnimationCurveWrapperJSON:{"curve":{"serializedVersion":"2","m_Curve":[{"serializedVersion":"3","time":0.0,"value":0.0,"inSlope":3.990557909011841,"outSlope":3.990557909011841,"tangentMode":0,"weightedMode":0,"inWeight":0.0,"outWeight":0.28961747884750368},{"serializedVersion":"3","time":0.28773587942123415,"value":1.2619974613189698,"inSlope":-5.479894638061523,"outSlope":-5.479894638061523,"tangentMode":0,"weightedMode":0,"inWeight":0.3333333432674408,"outWeight":0.0346825085580349},{"serializedVersion":"3","time":0.5877358913421631,"value":1.104357361793518,"inSlope":-2.5090153217315676,"outSlope":-2.5090153217315676,"tangentMode":0,"weightedMode":0,"inWeight":0.14129972457885743,"outWeight":0.06758485734462738},{"serializedVersion":"3","time":0.800000011920929,"value":1.0,"inSlope":0.35952839255332949,"outSlope":0.0,"tangentMode":65,"weightedMode":0,"inWeight":0.3333333432674408,"outWeight":0.3333333432674408},{"serializedVersion":"3","time":1.0,"value":1.0,"inSlope":-0.048794765025377277,"outSlope":-0.048794765025377277,"tangentMode":0,"weightedMode":0,"inWeight":0.17802377045154572,"outWeight":0.0}],"m_PreInfinity":2,"m_PostInfinity":2,"m_RotationOrder":4}}
        private val spawnWidthCurve = BakedAnimationCurve.valueOf("0#2.945678#6.223669#9.809068#13.67697#17.80247#22.16067#26.72665#31.47553#36.38238#41.42232#46.57042#51.80179#57.09152#62.41472#67.74647#73.06187#78.33602#83.54401#88.66094#93.6619#98.52198#103.2163#107.7199#112.008#116.0555#119.8377#123.3296#126.5063#129.3428#131.8144#133.896#135.5628#136.7899#137.5524#137.8253#137.5838#136.8029#135.4578#133.5236#130.9753#127.7881#124.1065#120.6992#117.6499#114.9432#112.5637#110.4958#108.7239#107.2328#106.0067#105.0303#104.2881#103.7646#103.4442#103.3116#103.3511#103.5474#103.8848#104.3481#104.9215#105.5897#106.3372#107.1485#108.008#108.9003#109.8099#110.7214#111.6191#112.4877#113.3117#114.0755#114.7636#115.3606#115.851#116.2194#116.4501#116.5277#116.4367#116.1617#115.6871#114.9974#114.0773#112.9111#111.4833#109.8076#108.1884#106.7067#105.3573#104.1351#103.0349#102.0514#101.1794#100.4138#99.74934#99.18084#98.70307#98.31085#97.99898#97.76224#97.59544#97.49339#97.45087#97.4627#97.52367#97.62856#97.77219#97.94936#98.15486#98.38348#98.63005#98.88934#99.15617#99.42531#99.6916#99.94979#100.0007#100.0036#100.0083#100.0147#100.0226#100.0316#100.0416#100.0522#100.0633#100.0745#100.0858#100.0967#100.1071#100.1167#100.1253#100.1326#100.1384#100.1424#100.1444#100.1441#100.1414#100.1359#100.1274#100.1157#100.1005#100.0816#100.0587#100.0316#100")
        fun spawnWidthAnim(x: Float) = spawnWidthCurve.evaluate(x)

        // UnityEditor.AnimationCurveWrapperJSON:{"curve":{"serializedVersion":"2","m_Curve":[{"serializedVersion":"3","time":0.0,"value":0.0,"inSlope":7.944703102111816,"outSlope":7.944703102111816,"tangentMode":0,"weightedMode":0,"inWeight":0.0,"outWeight":0.04979035630822182},{"serializedVersion":"3","time":0.4000000059604645,"value":1.0,"inSlope":0.0,"outSlope":0.0,"tangentMode":0,"weightedMode":0,"inWeight":0.3333333432674408,"outWeight":0.3333333432674408},{"serializedVersion":"3","time":1.0,"value":1.0,"inSlope":0.0,"outSlope":0.0,"tangentMode":34,"weightedMode":0,"inWeight":0.3333333432674408,"outWeight":0.0}],"m_PreInfinity":2,"m_PostInfinity":2,"m_RotationOrder":4}}
        private val spawnHeightCurve = BakedAnimationCurve.valueOf("0#5.416626#10.63466#15.6578#20.48974#25.13419#29.59483#33.87537#37.97951#41.91094#45.67337#49.27048#52.70599#55.98358#59.10696#62.07982#64.90586#67.58878#70.13228#72.54005#74.8158#76.96322#78.98602#80.88788#82.6725#84.3436#85.90486#87.35998#88.71265#89.96658#91.12547#92.19302#93.17291#94.06887#94.88456#95.62369#96.28998#96.88711#97.41878#97.88869#98.30053#98.65801#98.96482#99.22467#99.44124#99.61826#99.75937#99.86834#99.94881#100.0045#100.0391#100.0564#100.0599#100.0535#100.0407#100.0254#100.0112#100.0018#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#100")
        fun spawnHeightAnim(x: Float): Float = spawnHeightCurve.evaluate(x)

        // UnityEditor.AnimationCurveWrapperJSON:{"curve":{"serializedVersion":"2","m_Curve":[{"serializedVersion":"3","time":0.0,"value":1.0,"inSlope":0.0,"outSlope":0.0,"tangentMode":0,"weightedMode":0,"inWeight":0.0,"outWeight":0.3333333432674408},{"serializedVersion":"3","time":0.10000000149011612,"value":1.0,"inSlope":0.0,"outSlope":-3.2075467109680177,"tangentMode":5,"weightedMode":0,"inWeight":0.4444444477558136,"outWeight":0.05625000223517418},{"serializedVersion":"3","time":0.6499999761581421,"value":0.0,"inSlope":-0.05660376697778702,"outSlope":0.0,"tangentMode":1,"weightedMode":0,"inWeight":0.5000000596046448,"outWeight":0.3333333432674408},{"serializedVersion":"3","time":1.0,"value":0.0,"inSlope":0.0,"outSlope":-0.6992231607437134,"tangentMode":1,"weightedMode":0,"inWeight":0.702380895614624,"outWeight":0.0}],"m_PreInfinity":2,"m_PostInfinity":2,"m_RotationOrder":4}}
        private val despawnWidthCurve = BakedAnimationCurve.valueOf("100#100#100#100#100#100#100#100#100#100#100#100#100#100#100#98.66675#96.45906#94.26961#92.09864#89.9464#87.81313#85.69909#83.60451#81.52966#79.47475#77.44007#75.42584#73.43231#71.45975#69.50838#67.57846#65.67023#63.78394#61.91985#60.07818#58.2592#56.46315#54.69028#52.94083#51.21506#49.5132#47.83552#46.18224#44.55363#42.94993#41.37138#39.81824#38.29074#36.78915#35.31369#33.86464#32.44222#31.04668#29.67828#28.33727#27.02387#25.73836#24.48096#23.25193#22.05153#20.87999#19.73756#18.62449#17.54102#16.48741#15.4639#14.47074#13.50817#12.57645#11.67581#10.80651#9.9688#9.162909#8.38911#7.647634#6.938725#6.262648#5.619633#5.009919#4.433787#3.89145#3.383178#2.909207#2.46979#2.06517#1.695591#1.36131#1.062566#0.7996142#0.5726933#0.3820539#0.227946#0.1106143#0.03029704#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0")
        fun despawnWidthAnim(x: Float): Float = despawnWidthCurve.evaluate(x)

        // UnityEditor.AnimationCurveWrapperJSON:{"curve":{"serializedVersion":"2","m_Curve":[{"serializedVersion":"3","time":0.0,"value":1.0,"inSlope":-2.547168731689453,"outSlope":-2.547168731689453,"tangentMode":0,"weightedMode":0,"inWeight":0.0,"outWeight":0.10000000149011612},{"serializedVersion":"3","time":0.5,"value":0.5,"inSlope":-0.05660367012023926,"outSlope":-3.0360195636749269,"tangentMode":1,"weightedMode":0,"inWeight":0.5,"outWeight":0.09166669845581055},{"serializedVersion":"3","time":1.0,"value":0.0,"inSlope":-0.7018868923187256,"outSlope":-0.7018868923187256,"tangentMode":0,"weightedMode":0,"inWeight":0.4166666269302368,"outWeight":0.0}],"m_PreInfinity":2,"m_PostInfinity":2,"m_RotationOrder":4}}
        private val despawnCurveHeight = BakedAnimationCurve.valueOf("100#98.25179#96.5446#94.87793#93.25129#91.6642#90.11618#88.60674#87.13539#85.70166#84.30504#82.94508#81.62125#80.3331#79.08013#77.86186#76.6778#75.52747#74.41039#73.32605#72.27399#71.25372#70.26475#69.30659#68.37876#67.48078#66.61216#65.77242#64.96105#64.1776#63.42157#62.69246#61.98981#61.31312#60.66191#60.03568#59.43397#58.85627#58.30212#57.77102#57.26247#56.77601#56.31115#55.8674#55.44426#55.04128#54.65794#54.29378#53.94829#53.62101#53.31143#53.0191#52.74349#52.48414#52.24057#52.01228#51.7988#51.59963#51.41429#51.24229#51.08316#50.93639#50.80153#50.67806#50.56552#50.4634#50.37125#50.28855#50.21481#50.14959#50.09236#50.04266#50#47.92783#45.92704#43.99628#42.13411#40.33916#38.61002#36.94531#35.34361#33.80354#32.3237#30.90268#29.53911#28.23156#26.97867#25.77901#24.6312#23.53384#22.48554#21.48489#20.53049#19.62097#18.7549#17.93091#17.14758#16.40354#15.69737#15.02768#14.39308#13.79216#13.22354#12.68581#12.17757#11.69744#11.24401#10.81588#10.41167#10.02997#9.669384#9.32852#9.005973#8.700359#8.410266#8.134309#7.871089#7.619205#7.377261#7.143855#6.917608#6.697106#6.480962#6.267768#6.056139#5.84467#5.631971#5.416641#5.197281#4.9725#4.740891#4.501071#4.251641#3.991196#3.718326#3.431666#3.129804#2.811346#2.474892#2.11904#1.742387#1.343566#0.92116#0.4737705#0")
        fun despawnHeightAnim(x: Float): Float = despawnCurveHeight.evaluate(x)
    }

    var maxTime = conductor.maxTime
    private val data = mutableMapOf<Track, DrawCall>()
    val time: Int get() = conductor.time

    // TEXTURES
    val trackBackground = trackAtlas.findRegion("background")
    val trackLine = trackAtlas.findRegion("line")
    val trackGlow = trackAtlas.findRegion("glow")
    val judgementLine = trackAtlas.findRegion("white")

    init {
        if (chart.endTime != null)
            maxTime = chart.endTime

        for (track in chart.tracks) {
            // Ensure despawn_time isn't lower than spawn_time + spawn_duration
            if (track.spawnDuration > 0f)
                track.despawnTime = track.spawnTime + max(track.despawnTime - track.spawnTime, track.spawnDuration)

            data[track] = DrawCall()
            // Ensure game doesn't end too soon
            maxTime = max(maxTime, track.despawnTime + track.despawnDuration + 1f.toMillis())
        }
    }

    fun act(delta: Float) {
        conductor.act(delta)
    }

    fun render(stage: Stage, batch: SpriteBatch) {
        val time = conductor.time
        val width = stage.width
        val height = stage.height
        val trackWidth = width * 0.115f
        val borderThick: Float = 3f.scaledStageX(stage)
        val centerThick: Float = 2f.scaledStageX(stage)
        val glowWidth: Float = 12f.scaledStageX(stage)
        val judgementThick: Float = 2f.scaledStageY(stage)

        for ((track, call) in data) {
            call.shouldDraw =
                time >= track.spawnTime && time <= track.despawnTime + track.despawnDuration

            if (!call.shouldDraw) continue
            call.animating = false
            var scaleX = 1f
            call.scaleY = 1f

            val sinceDespawn = time - track.despawnTime
            if (sinceDespawn >= 0) {
                val t = (sinceDespawn.toFloat() / track.despawnDuration).coerceIn(0f, 1f)
                scaleX = despawnWidthAnim(t)
                call.scaleY = despawnHeightAnim(t)
                call.animating = true
            }

            if (!call.animating && track.spawnDuration > 0) {
                val sinceSpawn = time - track.spawnTime
                if (sinceSpawn <= track.spawnDuration) {
                    val t = (sinceSpawn.toFloat() / track.spawnDuration).coerceIn(0f, 1f)
                    scaleX = spawnWidthAnim(t)
                    call.scaleY = spawnHeightAnim(t)
                    call.animating = true
                }
            }

            call.center = round(track.getPosition(time) * width)
            call.width = track.getWidth(time, trackWidth, glowWidth) * scaleX
            call.color.set(track.getColor(time))
        }

        batch.use {
            it.enableBlending()

            // LEFT & RIGHT GLOWS
            forEachDrawable(data) { call ->
                it.color = call.color
                val half = call.width * 0.5f

                it.draw(
                    trackGlow,
                    call.center - half - glowWidth,
                    height * call.scaleY.mapRange(0.1666f, 0f),
                    glowWidth,
                    height * call.scaleY
                )
                it.draw(
                    trackGlow,
                    call.center + half + glowWidth,
                    height * call.scaleY.mapRange(0.1666f, 0f),
                    -glowWidth,
                    height * call.scaleY
                )
            }
            // BACKGROUND
            forEachDrawable(data) { call ->
                it.color = call.color
                it.draw(
                    trackBackground,
                    call.center - call.width * 0.5f,
                    height * call.scaleY.mapRange(0.1666f, 0f),
                    call.width,
                    height * call.scaleY
                )
            }
            // LEFT & RIGHT BORDERS
            it.color = Color.WHITE
            forEachDrawable(data) { call ->
                val half = call.width * 0.5f
                val tall = (height * LINE_HEIGHT_MULTIPLIER) * call.scaleY
                val y = (height * LINE_POS_MULTIPLIER) - tall * 0.5f
                it.draw(trackLine, call.center - half, y, borderThick, tall)
                it.draw(trackLine, call.center + half - borderThick, y, borderThick, tall)
            }
            // BLACK CENTER
            forEachDrawable(data) { call ->
                it.color = it.color.set(0f, 0f, 0f, call.scaleY)
                val tall = (height * LINE_HEIGHT_MULTIPLIER) * call.scaleY
                val y = (height * LINE_POS_MULTIPLIER) - tall * 0.5f
                it.draw(trackLine, call.center - centerThick * 0.5f, y, centerThick, tall)
            }
            // JUDGEMENT LINE
            it.color = Color.WHITE
            it.draw(
                judgementLine,
                0f,
                height * LINE_POS_MULTIPLIER - judgementThick * 0.5f,
                width,
                judgementThick
            )
            // NOTES
            // EFFECTS
        }
    }

    override fun dispose() {
        conductor.dispose()
        trackAtlas.dispose()
    }

    fun setPaused(paused: Boolean) {
        conductor.paused = paused
    }

    fun getPaused() = conductor.paused

    private fun forEachDrawable(data: MutableMap<Track, DrawCall>, action: (DrawCall) -> Unit) {
        for (call in data.values) {
            if (call.shouldDraw)
                action(call)
        }
    }
}