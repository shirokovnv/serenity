package modules.terrain.tiled

import core.math.Vector2
import core.scene.Object
import core.scene.volumes.BoxAABB
import modules.terrain.heightmap.Heightmap

class TiledTerrainPatch(
    private val heightmap: Heightmap,
    private val location: Vector2,
    private val size: Vector2
    ): Object() {

    init {
        recalculateBounds()
    }

    override fun recalculateBounds() {
        val xzOffset = Vector2(heightmap.worldOffset().x, heightmap.worldOffset().z)
        val xzScale = Vector2(heightmap.worldScale().x, heightmap.worldScale().z)

        val minPoint = xzOffset + location * xzScale
        val maxPoint = minPoint + size * xzScale
        val bounds = heightmap.calculatePatchBounds(
            minPoint, maxPoint
        )

        getComponent<BoxAABB>()!!.setShape(bounds.shape())
    }
}