package modules.terrain.heightmap.filters

import kotlin.math.abs

class RidgedFilter : HeightFilterInterface {
    override fun filter(x: Int, y: Int, height: Float): Float {
        val ridgedHeight = 1f - abs(height * 2.0f - 1.0f)
        return ridgedHeight * ridgedHeight
    }
}