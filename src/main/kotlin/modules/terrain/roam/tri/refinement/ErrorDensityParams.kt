package modules.terrain.roam.tri.refinement

class ErrorDensityParams : RefinementParams() {
    companion object {
        const val DEFAULT_DENSITY = 24.0f
        const val MIN_DENSITY = 1.0f
        const val MAX_DENSITY = 32.0f

        const val DEFAULT_VARIANCE_THRESHOLD = 0.001f
        const val MIN_VARIANCE_THRESHOLD = 0.0f
        const val MAX_VARIANCE_THRESHOLD = 1.0f
    }

    var density: Float = DEFAULT_DENSITY
    var varianceThreshold: Float = DEFAULT_VARIANCE_THRESHOLD
}