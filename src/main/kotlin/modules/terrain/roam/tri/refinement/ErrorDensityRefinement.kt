package modules.terrain.roam.tri.refinement

import core.math.SQRT2
import core.math.extensions.toRadians
import core.math.helpers.distance
import core.scene.camera.Camera
import core.scene.camera.PerspectiveCamera
import modules.terrain.roam.tri.TriNode
import kotlin.math.max
import kotlin.math.tan

class ErrorDensityRefinement(override val params: ErrorDensityParams, override val camera: Camera) :
    BaseRefinement() {

    override val type: RefinementType
        get() = RefinementType.DENSITY

    private val halfTanFov: Float
        get() = tan(((camera as PerspectiveCamera).fovY * 0.5f).toRadians())

    override fun splitCriteria(tri: TriNode): Boolean {
        val distance = distance(camera.position(), tri.geometry.center)

        val variance = max(
            tri.geometry.variance,
            tri.baseNeighbour?.geometry?.variance ?: 0f
        )

        if (variance < params.varianceThreshold) return false

        val triSize = tri.geometry.triSize * SQRT2
        val s: Float = 2 * distance * halfTanFov

        val diamondCriteria = if (tri.baseNeighbour != null && tri.baseNeighbour!!.index < tri.index)
            splitCriteria(tri.baseNeighbour!!)
        else true

        return (params.density * triSize > s) && diamondCriteria
    }

    override fun mergeCriteria(tri: TriNode): Boolean {
        val distance = distance(camera.position(), tri.geometry.center)

        val triSize = tri.geometry.triSize * SQRT2
        val s: Float = 2 * distance * halfTanFov

        val variance = max(
            tri.geometry.variance,
            tri.baseNeighbour?.geometry?.variance ?: 0f
        )

        if (variance < params.varianceThreshold) return true

        val diamondCriteria = if (tri.baseNeighbour != null && tri.baseNeighbour!!.index < tri.index)
            mergeCriteria(tri.baseNeighbour!!)
        else true

        return (params.density * triSize < s) && diamondCriteria
    }
}