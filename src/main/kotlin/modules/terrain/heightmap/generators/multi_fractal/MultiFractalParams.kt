package modules.terrain.heightmap.generators.multi_fractal

import core.math.noise.OctaveNoiseParams
import core.math.noise.PerlinNoise
import modules.terrain.heightmap.filters.FilterInterface
import modules.terrain.heightmap.generators.HeightmapGenerationParams

data class MultiFractalParams(
    val noise: PerlinNoise,
    val noiseParams: OctaveNoiseParams,
    val filters: List<FilterInterface>,
    override val normalize: Boolean = true
) : HeightmapGenerationParams