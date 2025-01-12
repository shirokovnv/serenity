package modules.terrain.tiled

import core.scene.Object
import modules.terrain.TerrainNormalRenderer

class TiledTerrain(config: TiledTerrainConfig) : Object() {

    init {
        addComponent(TerrainNormalRenderer(config.heightmap))
        addComponent(TiledTerrainBehaviour(config))
    }
}