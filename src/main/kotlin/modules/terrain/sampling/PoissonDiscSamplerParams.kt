package modules.terrain.sampling

import core.math.Vector2

data class PoissonDiscSamplerParams(
    val radius: Float,
    val sampleRegionSize: Vector2,
    val numSamplesBeforeRejection: Int = 30
)