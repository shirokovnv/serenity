package modules.terrain

const val MAX_ELEVATION_DATA_COUNT = 4

data class ElevationData(
    val minElevation: Float,
    val maxElevation: Float,
    val minSlope: Float,
    val maxSlope: Float,
    val strength: Float
) {
    init {
        require(minElevation in 0f..1f) {
            "minElevation must be between 0 and 1, but was $minElevation"
        }

        require(maxElevation in 0f..1f) {
            "maxElevation must be between 0 and 1, but was $maxElevation"
        }

        require(minSlope in -1f..1f) {
            "minNormal must be between 0 and 1, but was $minSlope"
        }

        require(maxSlope in -1f..1f) {
            "maxNormal must be between 0 and 1, but was $maxSlope"
        }

        require(strength in 1f..40f) {
            "strength must be between 0 and 1, but was $strength"
        }
    }
}