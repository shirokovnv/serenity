package graphics.model

import core.math.Matrix4
import core.math.Quaternion
import core.math.Vector3
import graphics.assets.surface.BaseMaterial

class ModelMaterial : BaseMaterial<ModelMaterial, ModelShader>() {
    lateinit var worldMatrix: Matrix4
    lateinit var worldViewProjection: Matrix4
    lateinit var clipPlane: Quaternion

    var isInstanced: Boolean = false
    var opacity: Float = 1.0f
        set(value) {
            field = value.coerceIn(0.0f, 1.0f)
        }
    var alphaThreshold: Float = 0.01f
    var isShadowPass: Boolean = false

    var mtlData: ModelMtlData? = null

    fun isTransparent(): Boolean = opacity < 1.0f
}