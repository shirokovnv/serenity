package modules.terrain.roam.tri.refinement

import core.math.helpers.distance
import core.scene.camera.Camera
import modules.terrain.roam.tri.TriNode

class ErrorDistanceRefinement(override val params: ErrorDistanceParams, override val camera: Camera) :
    BaseRefinement() {

    override val type: RefinementType
        get() = RefinementType.DISTANCE

    override fun splitCriteria(tri: TriNode): Boolean {
        val distance = distance(camera.position(), tri.geometry.center)
        val error = tri.geometry.variance
        return error * params.errorScale > distance * params.errorLimit * params.splitThreshold
    }

    override fun mergeCriteria(tri: TriNode): Boolean {
        val distance = distance(camera.position(), tri.geometry.center)
        val error = tri.geometry.variance
        return error * params.errorScale < distance * params.errorLimit * params.mergeThreshold
    }
}