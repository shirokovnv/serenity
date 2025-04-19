package modules.sky.dome

data class SkyDomeParams(
    val numRows: Int = 64,
    val numCols: Int = 64,
    val radius: Float = 10.0f,
    val yOffset: Float = 1f,
    val rotationSpeed: Float = 0.002f
)