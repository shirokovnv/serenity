package modules.terrain.heightmap

data class PlainParams(
    val yOffset: Float = 0.0f,
    override val normalize: Boolean = false
) : HeightmapGenerationParams {
    init {
        require(yOffset in 0.0f..1.0f)
    }
}