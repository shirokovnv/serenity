package graphics.model

import core.math.Matrix4
import graphics.assets.surface.BaseMaterial

class ModelMaterial: BaseMaterial<ModelMaterial, ModelShader>() {
    lateinit var worldViewProjection: Matrix4
    var isInstanced: Boolean = false
}