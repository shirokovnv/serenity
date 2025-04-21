package modules.terrain.marching_cubes.visuals

import core.math.Matrix4
import core.math.Vector3
import graphics.assets.surface.BaseMaterial

class NormalVisualizerMaterial : BaseMaterial<NormalVisualizerMaterial, NormalVisualizerShader>() {
    lateinit var world: Matrix4
    lateinit var viewProjection: Matrix4
    lateinit var color: Vector3
    var opacity: Float = 1.0f
}