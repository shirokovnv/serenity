package modules.terrain.quadtree

class QuadTreeLoDConfig {
    companion object {
        const val DEFAULT_MIN_TESS_FACTOR = 1
        const val DEFAULT_MAX_TESS_FACTOR = 16
        const val DEFAULT_TESS_FACTOR = 1

        const val DEFAULT_MIN_DISTANCE_MULTIPLIER = 1.0f
        const val DEFAULT_MAX_DISTANCE_MULTIPLIER = 20.0f
        const val DEFAULT_DISTANCE_MULTIPLIER = 5.0f
    }

    var maxDepth: Int = 9
    var distanceMultiplier: Float = DEFAULT_DISTANCE_MULTIPLIER
    var tessFactor: Int = DEFAULT_TESS_FACTOR
}