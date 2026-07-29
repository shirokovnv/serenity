package modules.terrain.quadtree

import core.management.Resources
import graphics.assets.texture.TextureFactory
import modules.terrain.BaseTerrainScene
import modules.terrain.TerrainSceneParams
import modules.terrain.heightmap.Heightmap

class QuadTreeTerrainScene(params: TerrainSceneParams): BaseTerrainScene(params) {
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

        val quadTreeTerrain = QuadTreeTerrain(
            QuadTreeTerrainConfig(
                heightmap,
                params.worldScale,
                params.worldOffset
            ),
            QuadTreeLoDConfig()
        )
        scene.attachToRoot(quadTreeTerrain)
    }
}