package graphics.rendering.gizmos

import core.math.Matrix4
import core.math.Vector3
import graphics.assets.surface.BaseMaterial

class BoxAABBMaterial : BaseMaterial<BoxAABBMaterial, BoxAABBShader>() {
    lateinit var boxCenter: Vector3
    lateinit var boxSize: Vector3
    lateinit var boxColor: Vector3
    lateinit var viewProjection: Matrix4
}