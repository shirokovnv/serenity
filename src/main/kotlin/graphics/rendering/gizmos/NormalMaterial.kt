package graphics.rendering.gizmos

import core.math.Matrix4
import core.math.Vector3
import graphics.assets.surface.BaseMaterial

class NormalMaterial : BaseMaterial<NormalMaterial, NormalShader>() {
    lateinit var world: Matrix4
    lateinit var viewProjection: Matrix4
    lateinit var color: Vector3
    var opacity: Float = 1.0f
}