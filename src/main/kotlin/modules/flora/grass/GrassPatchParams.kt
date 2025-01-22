package modules.flora.grass

data class GrassPatchParams(
    val spacing: Float,
    val verticalScale: Float,
    val cellSize: Int,
    val viewRange: Float
) {
    init {
        require(spacing in 0.0f..1.0f)
        require(verticalScale in 1.0f..20.0f)
        require(cellSize in 2..100)
        require(viewRange > 0.0f)
    }
}