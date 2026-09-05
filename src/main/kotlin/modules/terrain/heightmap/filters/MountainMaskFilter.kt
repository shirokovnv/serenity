package modules.terrain.heightmap.filters

import core.math.extensions.clamp

class MountainMaskFilter(
    private val blendThreshold: Float = 0.05f,
    private val transitionZone: Float = 0.15f
) : HeightFilterInterface {
    override fun filter(x: Int, y: Int, height: Float): Float {
        var mountainMask = ((height - blendThreshold) / transitionZone).clamp(0f, 1f)
        mountainMask *= mountainMask * (3.0f - 2.0f * mountainMask)

        return height * mountainMask
    }
}