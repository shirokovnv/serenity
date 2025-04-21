package modules.terrain.marching_cubes

import core.math.Matrix4
import core.math.Vector3
import graphics.assets.surface.BaseMaterial

class MarchingCubesMaterial : BaseMaterial<MarchingCubesMaterial, MarchingCubesShader>() {
    lateinit var world: Matrix4
    lateinit var viewProjection: Matrix4
    lateinit var lightDirection: Vector3
    lateinit var lightColor: Vector3
    lateinit var colorOne: Vector3
    lateinit var colorTwo: Vector3

    var ambientStrength: Float = 0.2f
    var diffuseStrength: Float = 1.0f

}