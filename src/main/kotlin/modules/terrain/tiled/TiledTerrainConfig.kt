package modules.terrain.tiled

import core.math.Vector3
import modules.terrain.heightmap.Heightmap

data class TiledTerrainConfig(
    val heightmap: Heightmap,
    val gridSize: Int,
    val worldScale: Vector3,
    val worldOffset: Vector3)