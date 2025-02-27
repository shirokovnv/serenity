package modules.fauna

import core.math.Matrix4
import graphics.animation.MtlData
import graphics.assets.surface.BaseMaterial

class AnimalMaterial : BaseMaterial<AnimalMaterial, AnimalShader>() {
    lateinit var model: Matrix4
    lateinit var view: Matrix4
    lateinit var projection: Matrix4

    var mtlData: MtlData? = null
    var boneTransforms = mutableListOf<Matrix4>()

    var isShadowPass: Boolean = false
}