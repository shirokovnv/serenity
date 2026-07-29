package modules.water.ocean

data class OceanParams(
    val meshResolution: Int,
    val fftResolution: Int,
    val amplitude: Float,
    val windAngle : Float,
    val windMagnitude: Float,
    val choppiness: Float
) {
    companion object {
        fun createDefault(): OceanParams {
            return OceanParams(
                512,
                256,
                10.0f,
                45.0f,
                10.0f,
                0.5f
            )
        }
    }
}