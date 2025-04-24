package modules.terrain.marching_cubes

import core.math.Vector3

data class MarchingCubesGridParams(
    val resolution: Int,
    val isoLevel: Float
) {
    companion object {
        const val MIN_RESOLUTION = 1
        const val MAX_RESOLUTION = 100

        const val MIN_ISO_LEVEL = -1.0f
        const val MAX_ISO_LEVEL = 1.0f
    }
}

data class MarchingCubesNoiseParams(
    val frequency: Float,
    val amplitude: Float,
    val lacunarity: Float,
    val persistence: Float,
    val octaves: Int
) {
    companion object {
        const val MIN_FREQUENCY = 0.1f
        const val MAX_FREQUENCY = 5.0f

        const val MIN_AMPLITUDE = 1.0f
        const val MAX_AMPLITUDE = 10.0f

        const val MIN_LACUNARITY = 0.1f
        const val MAX_LACUNARITY = 5.0f

        const val MIN_PERSISTENCE = 0.1f
        const val MAX_PERSISTENCE = 2.0f

        const val MIN_OCTAVES = 1
        const val MAX_OCTAVES = 10
    }
}

data class MarchingCubesExtraParams(
    val isTerracingEnabled: Boolean,
    val terraceHeight: Float,
    val isWarpingEnabled: Boolean,
    val warpFactor: Int,
    val isPlanetizingEnabled: Boolean,
    val planetRadius: Float,
    val planetStrength: Float,
    val colorOne: Vector3,
    val colorTwo: Vector3,
    val ambientOcclusion: Float
) {
    companion object {
        const val MIN_TERRACE_HEIGHT = 0.0f
        const val MAX_TERRACE_HEIGHT = 1.0f

        const val MIN_WARP_FACTOR = 1
        const val MAX_WARP_FACTOR = 16

        const val MIN_PLANET_RADIUS = 0.1f
        const val MAX_PLANET_RADIUS = 5.0f

        const val MIN_PLANET_STRENGTH = 1f
        const val MAX_PLANET_STRENGTH = 20f

        const val MIN_AMBIENT_OCCLUSION = 0.0f
        const val MAX_AMBIENT_OCCLUSION = 1.0f
    }
}