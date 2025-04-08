package graphics.model

import core.math.Matrix4
import core.math.Quaternion
import graphics.assets.surface.BaseMaterial

class ModelMaterial: BaseMaterial<ModelMaterial, ModelShader>() {
    lateinit var worldMatrix: Matrix4
    lateinit var worldViewProjection: Matrix4
    lateinit var clipPlane: Quaternion
    var isInstanced: Boolean = false
    var alphaThreshold: Float = 0.01f
    var isShadowPass: Boolean = false

    var mtlData: ModelMtlData? = null
}