package modules.terrain.roam.tri.refinement

abstract class RefinementParams {
    companion object {
        const val DEFAULT_LOD = 12
        const val MIN_LOD = 5
        const val MAX_LOD = 18

        const val DEFAULT_CULL_DIST_THRESHOLD = 200.0f
        const val MIN_CULL_DIST_THRESHOLD = 0.0f
        const val MAX_CULL_DIST_THRESHOLD = 500.0f
    }

    var maxLOD: Int = DEFAULT_LOD
    var cullDistThreshold: Float = DEFAULT_CULL_DIST_THRESHOLD
}