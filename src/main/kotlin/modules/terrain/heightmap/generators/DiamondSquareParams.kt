package modules.terrain.heightmap.generators

import modules.terrain.heightmap.generators.HeightmapGenerationParams

data class DiamondSquareParams(
    val roughness: Float,
    val maskOffset: Float,
    override val normalize: Boolean = false
) : HeightmapGenerationParams