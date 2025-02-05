package core.scene.spatial

import core.math.Rect3d
import core.math.Vector3
import core.math.helpers.highestBitSet
import core.scene.volumes.BoxAABB
import core.scene.Object
import kotlin.math.max
import kotlin.math.min

class LinearQuadTree : SpatialPartitioningInterface {
    enum class Constants(val value: Int) {
        MIN_TREE_DEPTH(1),
        MAX_TREE_DEPTH(9)
    }

    private val levelNodes: Array<Array<LinearQuadTreeNode?>?> =
        arrayOfNulls(Constants.MAX_TREE_DEPTH.value)

    private var worldExtents: Vector3 = Vector3(0f, 0f, 0f)
    private var worldScale: Vector3 = Vector3(0f, 0f, 0f)
    private var worldOffset: Vector3 = Vector3(0f, 0f, 0f)
    private var depth: Int = 0
    private var isReady: Boolean = false

    fun create(worldBoundingBox: Rect3d, depth: Int) {
        check(!isReady()) { "The quad tree has already been created." }
        check(depth >= Constants.MIN_TREE_DEPTH.value) { "Invalid tree depth." }
        check(depth <= Constants.MAX_TREE_DEPTH.value) { "Invalid tree depth." }

        this.depth = depth
        this.worldExtents = worldBoundingBox.size()
        this.worldOffset = Vector3(-worldBoundingBox.min.x, -worldBoundingBox.min.y, -worldBoundingBox.min.z)

        this.worldScale = Vector3(
            256.0f / worldExtents.x,
            32.0f / worldExtents.y,
            256.0f / worldExtents.z,
        )

        for (i in 0..<depth) {
            val nodeCount = (1 shl i) * (1 shl i)
            val nodes = arrayOfNulls<LinearQuadTreeNode>(nodeCount)
            levelNodes[i] = nodes
        }

        for (i in 0..<depth) {
            val levelDimension = 1 shl i
            var levelIndex = 0
            for (y in 0..<levelDimension) {
                for (x in 0..<levelDimension) {
                    val newNode = LinearQuadTreeNode()
                    levelNodes[i]!![levelIndex] = newNode

                    newNode.setup(
                        getNodeFromLevelXY(i - 1, x shr 1, y shr 1),
                        getNodeFromLevelXY(i + 1, x shl 1, y shl 1),
                        getNodeFromLevelXY(i + 1, (x shl 1) + 1, y shl 1),
                        getNodeFromLevelXY(i + 1, x shl 1, (y shl 1) + 1),
                        getNodeFromLevelXY(i + 1, (x shl 1) + 1, (y shl 1) + 1)
                    )
                    levelIndex++
                }
            }
        }

        isReady = true
    }

    fun destroy() {
        for(nodes in levelNodes) {
            nodes?.fill(null)
        }
        levelNodes.fill(null)
        depth = 0
        isReady = false
    }

    fun isReady(): Boolean {
        return isReady
    }

    override fun insert(obj: Object): Boolean {
        val objRect = obj.getComponent<BoxAABB>()!!.shape()

        val byteRect = buildByteRect(objRect)
        val node = findTreeNode(byteRect) ?: throw RuntimeException("FAILED TO LOCATE QUAD NODE")

        return node.addOrUpdateMember(obj, byteRect)
    }

    override fun remove(obj: Object): Boolean {
        val objRect = obj.getComponent<BoxAABB>()!!.shape()

        val byteRect = buildByteRect(objRect)
        val node = findTreeNode(byteRect) ?: throw RuntimeException("FAILED TO LOCATE QUAD NODE")

        return node.removeMember(obj)
    }

    override fun countObjects(): Int {
        var count = 0
        for(nodes in levelNodes) {
            count += nodes?.filterNotNull()?.sumOf { it.countObjects() } ?: 0
        }
        return count
    }

    override fun buildSearchResults(searchVolume: BoxAABB): List<Object> {
        val searchRect = searchVolume.shape()
        val byteRect = buildByteRect(searchRect)

        var level = 0
        val result = mutableListOf<Object>()

        while (level < depth) {
            val shiftCount = 8 - level
            val localRect = LinearQuadTreeRect(
                byteRect.x0 shr shiftCount,
                byteRect.x1 shr shiftCount,
                0,
                0,
                byteRect.z0 shr shiftCount,
                byteRect.z1 shr shiftCount,
            )

            for (y in localRect.z0..localRect.z1) {
                for (x in localRect.x0..localRect.x1) {
                    val node = getNodeFromLevelXY(level, x, y)

                    if (node != null) {
                        result.addAll(node.findCollisions(searchRect))
                    }
                }
            }
            level++
        }

        return result
    }

    private fun getNodeFromLevelXY(level: Int, x: Int, y: Int): LinearQuadTreeNode? {
        if (level < 0 || level >= depth) return null
        return levelNodes[level]?.getOrNull((y shl level) + x)
    }

    private fun findTreeNodeInfo(worldByteRect: LinearQuadTreeRect): LinearQuadTreeNodeInfo {
        val xPattern = worldByteRect.x0 xor worldByteRect.x1
        val yPattern = worldByteRect.z0 xor worldByteRect.z1

        val bitPattern = max(xPattern, yPattern)
        val highBit = if (bitPattern != 0) highestBitSet(bitPattern) + 1 else 0

        var level = Constants.MAX_TREE_DEPTH.value - highBit - 1
        level = min(level, depth - 1)

        val shift = Constants.MAX_TREE_DEPTH.value - level - 1

        val levelX = worldByteRect.x1 shr shift
        val levelY = worldByteRect.z1 shr shift

        return LinearQuadTreeNodeInfo(levelX, levelY, level)
    }

    private fun findTreeNode(worldByteRect: LinearQuadTreeRect): LinearQuadTreeNode? {
        val treeNodeInfo = findTreeNodeInfo(worldByteRect)

        val level = treeNodeInfo.level
        val levelX = treeNodeInfo.x
        val levelY = treeNodeInfo.y

        return getNodeFromLevelXY(level, levelX, levelY)
    }

    private fun buildByteRect(worldRect: Rect3d): LinearQuadTreeRect {
        val worldByteRect = LinearQuadTreeRect()

        return worldByteRect.convert(worldRect, worldOffset, worldScale)
    }
}