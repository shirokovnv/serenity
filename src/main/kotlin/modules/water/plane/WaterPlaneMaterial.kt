package modules.water.plane

import core.math.Matrix4
import core.math.Vector3
import graphics.assets.surface.BaseMaterial
import graphics.assets.texture.Texture2d

class WaterPlaneMaterial : BaseMaterial<WaterPlaneMaterial, WaterPlaneShader>() {
    lateinit var worldMatrix: Matrix4
    lateinit var localMatrix: Matrix4
    lateinit var viewProjection: Matrix4

    lateinit var reflectionMap: Texture2d
    lateinit var refractionMap: Texture2d
    lateinit var depthMap: Texture2d
    lateinit var dudvMap: Texture2d
    lateinit var normalMap: Texture2d

    lateinit var cameraPosition: Vector3
    lateinit var lightPosition: Vector3
    lateinit var lightColor: Vector3

    var moveFactor: Float = 0.0f
    var worldHeight: Float = 0.0f
    var near: Float = 1.0f
    var far: Float = 1000.0f
}