package modules.terrain.tiled

import core.math.Vector2
import core.scene.Object
import core.scene.volumes.BoxAABB
import modules.terrain.ElevationData
import modules.terrain.TerrainBlendRenderer
import modules.terrain.TerrainNormalRenderer

class TiledTerrain(private val config: TiledTerrainConfig, enablePostProcessing: Boolean) : Object() {

    init {
        val grassElevationData = ElevationData(0f, 1f, -1f, 1f, 1f)
        val dirtElevationData = ElevationData(0.2f, 1f, 0.75f, 1f, 10f)
        val rockElevationData = ElevationData(0.0f, 1f, 0f, 0.5f, 20f)

        val elevationData = arrayOf(
            grassElevationData,
            dirtElevationData,
            rockElevationData
        )

        addComponent(TerrainNormalRenderer(config.heightmap))
        addComponent(TerrainBlendRenderer(config.heightmap, elevationData))
        addComponent(TiledTerrainBehaviour(config, enablePostProcessing))

        recalculateBounds()
    }

    override fun recalculateBounds() {
        val minP = Vector2(
            config.worldOffset.x,
            config.worldOffset.z
        )
        val maxP = Vector2(
            config.worldOffset.x + config.worldScale.x,
            config.worldOffset.z + config.worldScale.z
        )

        val bounds = config.heightmap.calculatePatchBounds(
            minP,
            maxP
        )

        getComponent<BoxAABB>()!!.setShape(bounds.shape())
    }
}