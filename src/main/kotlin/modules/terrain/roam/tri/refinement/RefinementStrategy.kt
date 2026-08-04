package modules.terrain.roam.tri.refinement

import modules.terrain.roam.tri.TriNode

interface RefinementStrategy {
    fun shouldSplit(tri: TriNode): Boolean
    fun shouldMerge(tri: TriNode): Boolean
}