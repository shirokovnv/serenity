package modules.terrain.tiled

import core.scene.Object
import modules.terrain.ElevationData
import modules.terrain.TerrainBlendRenderer
import modules.terrain.TerrainNormalRenderer

class TiledTerrain(config: TiledTerrainConfig) : Object() {

    init {
        val grassElevationData = ElevationData(0f, 1f, -1f, 1f, 1f)
        val dirtElevationData = ElevationData(0f, 1f, 0f, 0.55f, 10f)
        val rockElevationData = ElevationData(0.5f, 1f, 0.35f, 1f, 20f)

        val elevationData = arrayOf(
            grassElevationData,
            dirtElevationData,
            rockElevationData
        )

        config.heightmap.getTexture().bilinearFilter()

        addComponent(TerrainNormalRenderer(config.heightmap))
        addComponent(TerrainBlendRenderer(config.heightmap, elevationData))
        addComponent(TiledTerrainRenderer(config))
    }
}