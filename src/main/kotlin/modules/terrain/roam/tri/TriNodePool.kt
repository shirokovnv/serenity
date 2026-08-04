package modules.terrain.roam.tri

import modules.terrain.roam.tri.refinement.RefinementParams
import kotlin.math.pow

class TriNodePool {
    companion object {
        private val PoolSize = (2.0f.pow(RefinementParams.MAX_LOD + 2) - 1).toInt()
    }

    private val pool: Array<TriNode> = Array(PoolSize) { TriNode() }

    fun size(): Int {
        return PoolSize
    }

    fun allocate(index: Int): TriNode {
        require(index > 0)
        val node = pool[index]
        node.index = index

        return node
    }

    fun release(index: Int) {
        require(index > 0)
        pool[index].clear()
    }
}