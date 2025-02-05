package graphics.rendering.gizmos

import core.math.Matrix4
import core.math.Vector3
import graphics.assets.surface.BaseMaterial
import kotlin.properties.Delegates

class RayMaterial : BaseMaterial<RayMaterial, RayShader>() {
    lateinit var rayOrigin: Vector3
    lateinit var rayDirection: Vector3
    var rayLength by Delegates.notNull<Float>()
    lateinit var rayColor: Vector3
    lateinit var viewProjection: Matrix4
}