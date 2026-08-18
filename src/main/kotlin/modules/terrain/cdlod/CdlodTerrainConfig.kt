package modules.terrain.cdlod

import core.math.Vector3
import modules.terrain.heightmap.Heightmap
import kotlin.properties.Delegates

class CdlodTerrainConfig {
    companion object {
        val RESOLUTIONS = arrayOf(4, 8, 16, 32, 64)
        const val DEFAULT_RESOLUTION = 16

        const val DEFAULT_MIN_DISTANCE_MULTIPLIER = 1.0f
        const val DEFAULT_MAX_DISTANCE_MULTIPLIER = 20.0f
        const val DEFAULT_DISTANCE_MULTIPLIER = 2.0f
    }

    private val onResolutionChangedListeners = mutableListOf<(Int) -> Unit>()
    private val onDistanceMultiplierChangedListeners = mutableListOf<(Float) -> Unit>()

    lateinit var heightmap: Heightmap
    lateinit var worldScale: Vector3
    lateinit var worldOffset: Vector3
    var maxLod by Delegates.notNull<Int>()

    var resolution: Int = DEFAULT_RESOLUTION
        set(value) {
            if (field != value) {
                field = value
                onResolutionChangedListeners.forEach { it(value) }
            }
        }

    var distanceMultiplier = DEFAULT_DISTANCE_MULTIPLIER
        set(value) {
            if (!field.equals(value)) {
                field = value
                onDistanceMultiplierChangedListeners.forEach { it(value) }
            }
        }

    fun addOnResolutionChanged(listener: (Int) -> Unit) =
        onResolutionChangedListeners.add(listener)

    fun removeOnResolutionChanged(listener: (Int) -> Unit) =
        onResolutionChangedListeners.remove(listener)

    fun addOnDistanceMultiplierChanged(listener: (Float) -> Unit) =
        onDistanceMultiplierChangedListeners.add(listener)

    fun removeOnDistanceMultiplierChanged(listener: (Float) -> Unit) =
        onDistanceMultiplierChangedListeners.remove(listener)
}