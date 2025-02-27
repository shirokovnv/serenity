package graphics.model

import core.math.Matrix4
import graphics.assets.surface.BaseMaterial

class ModelMaterial: BaseMaterial<ModelMaterial, ModelShader>() {
    lateinit var worldViewProjection: Matrix4
    var isInstanced: Boolean = false
    var alphaThreshold: Float = 0.01f
    var isShadowPass: Boolean = false

    var mtlData: ModelMtlData? = null
}