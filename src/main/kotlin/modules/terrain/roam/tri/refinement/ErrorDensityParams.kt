package modules.terrain.roam.tri.refinement

class ErrorDensityParams : RefinementParams() {
    companion object {
        const val DEFAULT_DENSITY = 24.0f
        const val MIN_DENSITY = 1.0f
        const val MAX_DENSITY = 32.0f
    }

    var density: Float = DEFAULT_DENSITY
}