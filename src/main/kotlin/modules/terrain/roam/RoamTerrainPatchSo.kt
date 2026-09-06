package modules.terrain.roam

import core.math.noise.OctaveNoiseParams
import core.math.noise.PerlinNoise
import core.scene.Object
import modules.terrain.ElevationData
import modules.terrain.TerrainBlendRenderer
import modules.terrain.TerrainNormalRenderer
import modules.terrain.heightmap.Heightmap
import modules.terrain.heightmap.filters.DomainWarpFilter
import modules.terrain.heightmap.filters.ErosionFilter
import modules.terrain.heightmap.filters.MountainMaskFilter
import modules.terrain.heightmap.filters.RidgedFilter
import modules.terrain.heightmap.generators.multi_fractal.MultiFractalGenerator
import modules.terrain.heightmap.generators.multi_fractal.MultiFractalParams
import modules.terrain.roam.tri.mesh.TriMeshScheme

class RoamTerrainPatchSo(
    private val config: RoamTerrainPatchConfig
) : Object() {
    private var patch: RoamTerrainPatch
    private var heightmap: Heightmap

    init {
        transform().setScale(config.worldScale)
        transform().setTranslation(config.worldOffset)

        heightmap = createHeightmap()
        patch = RoamTerrainPatch(
            config,
            heightmap,
            transform(),
            TriMeshScheme.MESH_VERTICES
        )

        val grass = ElevationData(0f, 1f, -1f, 1f, 1f)
        val dirt = ElevationData(0.0f, 0.5f, 0.75f, 1.0f, 5f)
        val rock = ElevationData(0.4f, 0.8f, 0f, 0.55f, 10f)
        val snow = ElevationData(0.6f, 1.0f, 0.75f, 1f, 20f)

        val elevationData = arrayOf(
            grass,
            dirt,
            rock,
            snow
        )

        addComponent(patch)
        addComponent(TerrainNormalRenderer(heightmap))
        addComponent(TerrainBlendRenderer(heightmap, elevationData))
        addComponent(RoamTerrainPatchBehaviour(config))
    }

    private fun createHeightmap() : Heightmap {
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
            config.worldOffset,
            config.worldScale,
        )

        heightmap.texture().bind()
        heightmap.texture().bilinearFilter()
        heightmap.texture().unbind()

        return heightmap
    }
}