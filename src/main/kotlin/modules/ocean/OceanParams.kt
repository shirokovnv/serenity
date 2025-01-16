package modules.ocean

data class OceanParams(
    val meshResolution: Int,
    val fftResolution: Int,
    val amplitude: Float,
    val windAngle : Float,
    val windMagnitude: Float,
    val choppiness: Float
)