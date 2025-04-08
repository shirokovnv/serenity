package modules.water.plane

data class WaterPlaneParams(
    val worldHeight: Float = WaterPlaneConstants.DEFAULT_WORLD_HEIGHT,
    val waveSpeed: Float = WaterPlaneConstants.DEFAULT_WAVE_SPEED
)