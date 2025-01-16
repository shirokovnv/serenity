package core.math.noise

data class OctaveNoiseParams(
    val scale: Float,
    val octave: Int,
    val amplitude: Float,
    val persistence: Float
) : NoiseParams