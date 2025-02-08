package graphics.rendering.gizmos

import core.math.Matrix4
import core.math.Vector3
import graphics.assets.surface.BaseMaterial
import kotlin.properties.Delegates

class SphereMaterial : BaseMaterial<SphereMaterial, SphereShader>() {
    lateinit var color: Vector3
    lateinit var center: Vector3
    var radius by Delegates.notNull<Float>()
    lateinit var viewProjection: Matrix4
}