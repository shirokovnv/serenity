package graphics.rendering.gizmos

import core.math.Matrix4
import core.math.Vector3
import graphics.assets.surface.BaseMaterial

class TriangleMaterial : BaseMaterial<TriangleMaterial, TriangleShader>() {
    lateinit var color: Vector3
    lateinit var viewProjection: Matrix4
}