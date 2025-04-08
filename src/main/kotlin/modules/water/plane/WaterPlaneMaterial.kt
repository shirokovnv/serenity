package modules.water.plane

import core.math.Matrix4
import graphics.assets.surface.BaseMaterial

class WaterPlaneMaterial : BaseMaterial<WaterPlaneMaterial, WaterPlaneShader>() {
    lateinit var worldMatrix: Matrix4
    lateinit var localMatrix: Matrix4
    lateinit var viewProjection: Matrix4

    var worldHeight: Float = 0.0f
}