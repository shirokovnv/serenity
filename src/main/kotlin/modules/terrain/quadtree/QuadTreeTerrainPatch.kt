package modules.terrain.quadtree

import core.math.Vector2
import core.scene.Object
import core.scene.volumes.BoxAABB
import modules.terrain.heightmap.Heightmap

class QuadTreeTerrainPatch(
    private val heightmap: Heightmap,
    private val topLeft: Vector2,
    private val edgeLength: Float
): Object() {
    init {
        recalculateBounds()
    }

    override fun recalculateBounds() {
        val xzOffset = Vector2(heightmap.worldOffset().x, heightmap.worldOffset().z)
        val xzScale = Vector2(heightmap.worldScale().x, heightmap.worldScale().z)

        val minPoint = xzOffset + topLeft * xzScale
        val maxPoint = minPoint + Vector2(edgeLength, edgeLength) * xzScale

        val bounds = heightmap.calculatePatchBounds(
            minPoint, maxPoint
        )

        getComponent<BoxAABB>()!!.setShape(bounds.shape())
    }
}