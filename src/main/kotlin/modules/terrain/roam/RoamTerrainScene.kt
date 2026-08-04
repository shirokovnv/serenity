package modules.terrain.roam

import modules.terrain.BaseTerrainScene
import modules.terrain.TerrainSceneParams

class RoamTerrainScene(params: TerrainSceneParams) : BaseTerrainScene(params) {
    override fun initializeTerrain() {
        val config = RoamTerrainConfig(params.worldScale, params.worldOffset)

        val roamTerrain = RoamTerrain(config)
        scene.attachToRoot(roamTerrain)
    }
}