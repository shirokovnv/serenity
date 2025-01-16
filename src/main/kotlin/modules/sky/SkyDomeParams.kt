package modules.sky

data class SkyDomeParams(
    val numRows: Int = 32,
    val numCols: Int = 32,
    val radius: Float = 10.0f,
    val yOffset: Float = 1f,
    val rotationSpeed: Float = 0.02f
)