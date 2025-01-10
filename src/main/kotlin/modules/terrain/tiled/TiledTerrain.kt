package modules.terrain.tiled

import core.scene.Object

class TiledTerrain(private val config: TiledTerrainConfig) : Object() {

    init {
        addComponent(TiledTerrainBehaviour(config))
    }
}