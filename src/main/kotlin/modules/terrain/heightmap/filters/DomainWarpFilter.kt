package modules.terrain.heightmap.filters

import core.math.Vector2
import core.math.noise.OctaveNoiseParams
import core.math.noise.PerlinNoise

class DomainWarpFilter(
    private val noise: PerlinNoise,
    private val noiseParams: OctaveNoiseParams,
    private val warpOffset: Vector2 = Vector2(64f, 64f),
    private val warpStrength: Float = 0.2f,
) : HeightFilterInterface {

    override fun filter(x: Int, y: Int, height: Float): Float {
        val wx = x.toFloat()
        val wy = y.toFloat()

        return height + noise.octaveNoise(
            wx + warpOffset.x,
            wy + warpOffset.y,
            noiseParams.scale,
            noiseParams.octave,
            noiseParams.amplitude,
            noiseParams.persistence
        ) * warpStrength
    }
}