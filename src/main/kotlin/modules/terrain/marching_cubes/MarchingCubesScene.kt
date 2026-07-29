package modules.terrain.marching_cubes

import core.math.Vector3
import core.scene.Transform
import modules.terrain.BaseTerrainScene
import modules.terrain.TerrainSceneParams

class MarchingCubesScene(params: TerrainSceneParams) : BaseTerrainScene(params) {
    override fun initializeTerrain() {
        val marchingCubes = MarchingCubes()
        marchingCubes.getComponent<Transform>()!!.setTranslation(Vector3(-500f, 0f, -500f))
        scene.attachToRoot(marchingCubes)
    }
}