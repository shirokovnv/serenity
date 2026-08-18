package modules.terrain.cdlod

import core.management.Resources
import graphics.assets.texture.TextureFactory
import modules.terrain.BaseTerrainScene
import modules.terrain.TerrainSceneParams
import modules.terrain.heightmap.Heightmap

class CdlodTerrainScene(params: TerrainSceneParams) : BaseTerrainScene(params) {
    override fun initializeTerrain() {
        val heightmap = Heightmap(
            TextureFactory.fromPerlinNoise(
                1024,
                1024,
                0.007f,
                5,
                1.0f,
                0.46f
            ),
            params.worldScale,
            params.worldOffset
        )
        Resources.put<Heightmap>(heightmap)

        val config = CdlodTerrainConfig()
        config.heightmap = heightmap
        config.worldScale = params.worldScale
        config.worldOffset = params.worldOffset
        config.distanceMultiplier = 5.0f
        config.resolution = 16
        config.maxLod = 9

        val cdlodTerrain = CdlodTerrain(config)
        scene.attachToRoot(cdlodTerrain)
    }
}