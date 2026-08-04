package modules.terrain.roam

import core.math.Vector3
import modules.terrain.roam.tri.refinement.BaseRefinement

class RoamTerrainPatchConfig {
    companion object {
        const val DEFAULT_PER_FRAME_UPDATE = 1
        const val MIN_FRAME_UPDATE = 1
        const val MAX_FRAME_UPDATE = 5

        const val DEFAULT_SPLITS = 1000
        const val MIN_SPLITS = 100
        const val MAX_SPLITS = 3000

        const val DEFAULT_MERGES = 1000
        const val MIN_MERGES = 100
        const val MAX_MERGES = 3000
    }

    lateinit var worldScale: Vector3
    lateinit var worldOffset: Vector3
    lateinit var refinement: BaseRefinement
    var perFrameUpdate: Int = DEFAULT_PER_FRAME_UPDATE
    var maxSplits: Int = DEFAULT_SPLITS
    var maxMerges: Int = DEFAULT_MERGES
}