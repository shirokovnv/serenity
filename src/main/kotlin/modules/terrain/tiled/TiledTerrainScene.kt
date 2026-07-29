package modules.terrain.tiled

import core.management.Resources
import modules.terrain.BaseTerrainScene
import modules.terrain.TerrainSceneParams
import modules.terrain.heightmap.DiamondSquareGenerator
import modules.terrain.heightmap.DiamondSquareParams
import modules.terrain.heightmap.Heightmap

class TiledTerrainScene(
    params: TerrainSceneParams
) : BaseTerrainScene(
    params
) {
    override fun initializeTerrain() {
        val heightmap = Heightmap.fromGenerator(
            DiamondSquareGenerator(),
            DiamondSquareParams(2f, 40f),
            1024,
            1024,
            params.worldOffset,
            params.worldScale
        )
        Resources.put<Heightmap>(heightmap)

        val tiledTerrain = TiledTerrain(
            TiledTerrainConfig(
                heightmap,
                16,
                params.worldScale,
                params.worldOffset
            ), false
        )
        scene.attachToRoot(tiledTerrain)
    }
}