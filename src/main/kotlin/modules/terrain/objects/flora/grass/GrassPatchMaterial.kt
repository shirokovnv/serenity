package modules.terrain.objects.flora.grass

import core.math.Matrix4
import graphics.assets.surface.BaseMaterial
import kotlin.properties.Delegates

class GrassPatchMaterial: BaseMaterial<GrassPatchMaterial, GrassPatchShader>() {
    lateinit var worldMatrix: Matrix4
    lateinit var viewMatrix: Matrix4
    lateinit var projMatrix: Matrix4
    var time by Delegates.notNull<Float>()
}