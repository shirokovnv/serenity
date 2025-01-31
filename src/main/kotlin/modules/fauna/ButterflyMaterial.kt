package modules.fauna

import core.math.Matrix4
import graphics.assets.surface.BaseMaterial
import graphics.assets.texture.Texture2d

class ButterflyMaterial: BaseMaterial<ButterflyMaterial, ButterflyShader>() {
    lateinit var model: Matrix4
    lateinit var view: Matrix4
    lateinit var projection: Matrix4

    var diffuseTexture: Texture2d? = null
    var boneTransforms = mutableListOf<Matrix4>()
}