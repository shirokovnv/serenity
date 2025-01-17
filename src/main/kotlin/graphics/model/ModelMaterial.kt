package graphics.model

import core.math.Matrix4
import graphics.assets.surface.BaseMaterial
import graphics.assets.texture.Texture2d

class ModelMaterial: BaseMaterial<ModelMaterial, ModelShader>() {
    lateinit var worldViewProjection: Matrix4
    var isInstanced: Boolean = false
    var alphaThreshold: Float = 0.01f

    var mtlData: ModelMtlData? = null
}