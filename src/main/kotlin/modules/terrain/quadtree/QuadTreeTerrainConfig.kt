package modules.terrain.quadtree

import core.math.Vector3
import modules.terrain.heightmap.Heightmap
import kotlin.math.max

data class QuadTreeTerrainConfig(
    val heightmap: Heightmap,
    val worldScale: Vector3,
    val worldOffset: Vector3
) {
    fun getXZScale(): Float {
        return max(worldScale.x, worldScale.z)
    }
}