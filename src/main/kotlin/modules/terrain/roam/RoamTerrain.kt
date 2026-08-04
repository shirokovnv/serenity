package modules.terrain.roam

import core.scene.Object

class RoamTerrain(config: RoamTerrainConfig): Object() {
    init {
        addComponent(RoamTerrainBehaviour(config))
    }
}