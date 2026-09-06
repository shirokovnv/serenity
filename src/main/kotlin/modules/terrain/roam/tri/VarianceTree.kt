package modules.terrain.roam.tri

import core.math.Triangle
import core.math.Vector3
import modules.terrain.heightmap.Heightmap
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow

class VarianceTree(
    private val heightmap: Heightmap,
    private val rootA: Triangle,
    private val rootB: Triangle,
    private val maxDepth: Int,
    ) {
    private var variance: FloatArray = FloatArray((2.0f.pow(maxDepth + 2) - 1).toInt())

    init {
        recursiveCalculateVariance(
            rootA.v0,
            rootA.v1,
            rootA.v2,
            1,
            0
        )

        recursiveCalculateVariance(
            rootB.v0,
            rootB.v1,
            rootB.v2,
            2,
            0
        )
    }

    fun getVariance(triNode: TriNode): Float {
        return variance[triNode.index]
    }

    fun getVariance(index: Int): Float {
        return variance[index]
    }

    private fun recursiveCalculateVariance(v0: Vector3, v1: Vector3, v2: Vector3, index: Int, depth: Int): Float {
        if (depth >= maxDepth) {
            return 0.0f
        }

        val midX = (v1.x + v2.x) * 0.5f
        val midZ = (v1.z + v2.z) * 0.5f

        val realH = heightmap.getInterpolatedHeight(midX, midZ) * heightmap.worldScale().y
        val planeH = (v1.y + v2.y) * 0.5f

        var currentMaxError = abs(realH - planeH) / heightmap.worldScale().y

        val midPoint = Vector3(midX, realH, midZ)

        val leftError = recursiveCalculateVariance(midPoint, v0, v1, (index shl 1) + 1, depth + 1)
        currentMaxError = max(currentMaxError, leftError)

        val rightError = recursiveCalculateVariance(midPoint, v2, v0, (index shl 1) + 2, depth + 1)
        currentMaxError = max(currentMaxError, rightError)

        variance[index] = currentMaxError

        return currentMaxError
    }
}