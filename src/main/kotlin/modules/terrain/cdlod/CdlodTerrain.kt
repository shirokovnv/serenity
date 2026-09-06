package modules.terrain.cdlod

import core.scene.Object
import modules.terrain.ElevationData
import modules.terrain.TerrainBlendRenderer
import modules.terrain.TerrainNormalRenderer

class CdlodTerrain(private val config: CdlodTerrainConfig) : Object() {
    init {
        val grass = ElevationData(0f, 1f, -1f, 1f, 1f)
        val dirt = ElevationData(0.0f, 0.5f, 0.75f, 1.0f, 5f)
        val rock = ElevationData(0.4f, 0.8f, 0f, 0.55f, 10f)
        val snow = ElevationData(0.6f, 1.0f, 0.75f, 1f, 20f)

        val elevationData = arrayOf(
            grass,
            dirt,
            rock,
            snow
        )

        addComponent(TerrainNormalRenderer(config.heightmap))
        addComponent(TerrainBlendRenderer(config.heightmap, elevationData))
        addComponent(CdlodTerrainBehaviour(config))
    }
}