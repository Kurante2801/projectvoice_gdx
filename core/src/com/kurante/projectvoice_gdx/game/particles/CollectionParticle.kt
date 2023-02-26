package com.kurante.projectvoice_gdx.game.particles

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool.Poolable
import com.kurante.projectvoice_gdx.util.BakedAnimationCurve

class CollectionParticle(
    var x: Float,
    private var y: Float,
    private var region: TextureRegion?,
    private var endSize: Float,
    private var endRotation: Float,
    var duration: Float, // Seconds
    var sizeAnim: BakedAnimationCurve = collectionSizeAnim
) : Poolable {
    companion object {
        val collectionAlphaAnim = BakedAnimationCurve.valueOf("100#0")
        // UnityEditor.AnimationCurveWrapperJSON:{"curve":{"serializedVersion":"2","m_Curve":[{"serializedVersion":"3","time":0.0,"value":0.619049072265625,"inSlope":1.0245310068130494,"outSlope":1.1682096719741822,"tangentMode":1,"weightedMode":2,"inWeight":0.0,"outWeight":0.26766499876976015},{"serializedVersion":"3","time":1.0,"value":1.0,"inSlope":0.08444180339574814,"outSlope":0.08444180339574814,"tangentMode":0,"weightedMode":0,"inWeight":0.15106743574142457,"outWeight":0.0}],"m_PreInfinity":2,"m_PostInfinity":2,"m_RotationOrder":4}}
        val collectionSizeAnim = BakedAnimationCurve.valueOf("61.90491#63.79837#65.59038#67.28866#68.90001#70.43049#71.88547#73.26981#74.58793#75.8438#77.041#78.18288#79.27242#80.31244#81.30549#82.25394#83.16004#84.02576#84.85307#85.64375#86.39947#87.12179#87.81219#88.47208#89.10278#89.70551#90.28146#90.83176#91.35749#91.8597#92.3393#92.79726#93.23449#93.65183#94.05013#94.43018#94.79275#95.1386#95.46846#95.78304#96.08302#96.36909#96.6419#96.90212#97.15036#97.38728#97.61346#97.82954#98.03614#98.23386#98.4233#98.60509#98.77983#98.94814#99.11065#99.26797#99.42078#99.56969#99.71541#99.8586#100")
        // UnityEditor.AnimationCurveWrapperJSON:{"curve":{"serializedVersion":"2","m_Curve":[{"serializedVersion":"3","time":0.0,"value":1.0,"inSlope":0.0,"outSlope":4.509847640991211,"tangentMode":1,"weightedMode":0,"inWeight":0.0,"outWeight":0.28409090638160708},{"serializedVersion":"3","time":0.2199999988079071,"value":0.0,"inSlope":-4.545454502105713,"outSlope":0.0,"tangentMode":69,"weightedMode":0,"inWeight":0.5000000596046448,"outWeight":0.3333333432674408},{"serializedVersion":"3","time":1.0,"value":0.0,"inSlope":0.0,"outSlope":-0.6992231607437134,"tangentMode":1,"weightedMode":0,"inWeight":0.702380895614624,"outWeight":0.0}],"m_PreInfinity":2,"m_PostInfinity":2,"m_RotationOrder":4}}
        val holdSizeEndAnim = BakedAnimationCurve.valueOf("100#105.3163#106.579#104.3077#99.02207#91.24189#81.48685#70.27664#58.13095#45.56952#33.11201#21.27817#10.58767#1.560187#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0")
    }

    var progress = 0f
    private var drawX = 0f
    private var drawY = 0f
    private var size = 0f
    private var alpha = 1f
    private var rotation = 0f

    fun act(delta: Float) {
        progress += duration * delta
        if (progress > duration) return

        val t = progress / duration
        size = sizeAnim.evaluate(t) * endSize
        alpha = collectionAlphaAnim.evaluate(t)
        rotation = endRotation * t

        drawX = x - size * 0.5f
        drawY = y - size * 0.5f
    }

    fun render(batch: Batch) {
        batch.color = batch.color.set(1f, 1f, 1f, alpha)
        batch.draw(region, drawX, drawY, size * 0.5f, size * 0.5f, size, size, 1f, 1f, rotation)
    }

    override fun reset() {
        sizeAnim = collectionSizeAnim
        rotation = 0f
    }

    fun initialize(x: Float, y: Float, region: TextureRegion, endSize: Float, endRotation: Float, duration: Float) {
        this.x = x
        this.y = y
        this.region = region
        this.endSize = endSize
        this.endRotation = endRotation
        this.duration = duration
        progress = 0f
    }
}