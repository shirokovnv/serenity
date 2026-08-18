package modules.terrain.cdlod

import core.scene.Object

class CdlodTerrain(private val config: CdlodTerrainConfig) : Object() {
    init {
        addComponent(CdlodTerrainBehaviour(config))
    }
}