package modules.terrain.navigation

import core.math.Matrix4
import graphics.assets.surface.BaseMaterial

class TerrainNavMeshMaterial : BaseMaterial<TerrainNavMeshMaterial, TerrainNavMeshShader>() {
    lateinit var viewProjection: Matrix4
    var yOffset: Float = 1.0f
    var opacity: Float = 0.25f
}