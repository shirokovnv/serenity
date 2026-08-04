package modules.terrain.roam.tri.refinement

import core.scene.camera.Camera

enum class RefinementType {
    DISTANCE,
    DENSITY,
}

object RefinementFactory {
    fun create(type: RefinementType, params: RefinementParams, camera: Camera): BaseRefinement {
        return when (type) {
            RefinementType.DISTANCE -> ErrorDistanceRefinement(params as ErrorDistanceParams, camera)
            RefinementType.DENSITY -> ErrorDensityRefinement(params as ErrorDensityParams, camera)
        }
    }
}