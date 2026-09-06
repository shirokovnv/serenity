package modules.terrain.cdlod

import core.management.Resources
import core.math.noise.OctaveNoiseParams
import core.math.noise.PerlinNoise
import modules.terrain.BaseTerrainScene
import modules.terrain.TerrainSceneParams
import modules.terrain.heightmap.Heightmap
import modules.terrain.heightmap.filters.DomainWarpFilter
import modules.terrain.heightmap.filters.ErosionFilter
import modules.terrain.heightmap.filters.MountainMaskFilter
import modules.terrain.heightmap.filters.RidgedFilter
import modules.terrain.heightmap.generators.multi_fractal.MultiFractalGenerator
import modules.terrain.heightmap.generators.multi_fractal.MultiFractalParams

class CdlodTerrainScene(params: TerrainSceneParams) : BaseTerrainScene(params) {
    override fun initializeTerrain() {
        val noise = PerlinNoise.defaultNoiseInstance
        val noiseParams = OctaveNoiseParams(
            0.005f,
            8,
            1.0f,
            0.35f,
        )

        val heightmap = Heightmap.fromGenerator(
            MultiFractalGenerator(),
            MultiFractalParams(
                noise,
                noiseParams,
                listOf(
                    DomainWarpFilter(noise, noiseParams),
                    RidgedFilter(),
                    MountainMaskFilter(),
                    ErosionFilter()
                )
            ),
            1024,
            1024,
            params.worldOffset,
            params.worldScale,
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