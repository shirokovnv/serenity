package modules.terrain.roam.tri.refinement

class ErrorDistanceParams : RefinementParams() {
    companion object {
        const val DEFAULT_ERROR_SCALE = 5.0f
        const val MIN_ERROR_SCALE = 0.1f
        const val MAX_ERROR_SCALE = 50.0f

        const val DEFAULT_ERROR_LIMIT = 0.0001f
        const val MIN_ERROR_LIMIT = 0.0001f
        const val MAX_ERROR_LIMIT = 0.001f

        const val DEFAULT_SPLIT_THRESHOLD = 1.4f
        const val MIN_SPLIT_THRESHOLD = 0.1f
        const val MAX_SPLIT_THRESHOLD = 5.0f

        const val DEFAULT_MERGE_THRESHOLD = 0.6f
        const val MIN_MERGE_THRESHOLD = 0.1f
        const val MAX_MERGE_THRESHOLD = 5.0f
    }

    var errorScale: Float = DEFAULT_ERROR_SCALE
    var errorLimit: Float = DEFAULT_ERROR_LIMIT

    var splitThreshold: Float = DEFAULT_SPLIT_THRESHOLD
    var mergeThreshold: Float = DEFAULT_MERGE_THRESHOLD
}