package modules.terrain.roam.tri.refinement

import core.scene.camera.Camera
import modules.terrain.roam.tri.TriNode

abstract class BaseRefinement : RefinementStrategy {
    abstract val type: RefinementType
    protected abstract val params: RefinementParams
    protected abstract val camera: Camera

    protected abstract fun splitCriteria(tri: TriNode): Boolean
    protected abstract fun mergeCriteria(tri: TriNode): Boolean

    fun params(): RefinementParams = params

    override fun shouldSplit(tri: TriNode): Boolean {
        return  tri.depth < params.maxLOD &&
                splitCriteria(tri) &&
                tri.leftChild == null
                && tri.rightChild == null
    }

    override fun shouldMerge(tri: TriNode): Boolean {
        return (tri.depth >= params.maxLOD || mergeCriteria(tri)) &&
                tri.leftChild != null &&
                tri.rightChild != null
    }
}