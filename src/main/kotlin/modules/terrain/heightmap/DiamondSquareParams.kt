package modules.terrain.heightmap

data class DiamondSquareParams(
    val roughness: Float,
    val maskOffset: Float,
    override val normalize: Boolean = false
) : HeightmapGenerationParams