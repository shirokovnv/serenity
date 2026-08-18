package modules.terrain.cdlod

import core.math.Vector2
import core.math.Vector3
import core.scene.spatial.QuadTreeCache
import core.scene.spatial.QuadTreeKey
import core.scene.spatial.QuadTreeNode
import core.scene.volumes.BoxAABB
import modules.terrain.quadtree.QuadTreeTerrainNode
import kotlin.math.max
import kotlin.math.pow

class CdlodTerrainSection(
    val config: CdlodTerrainConfig,
    val topLeft: Vector2,
    level: Int,
    val lodRanges: FloatArray
) : QuadTreeNode() {

    companion object {
        private val quadTreeCache = QuadTreeCache(1000, 30000L)
    }

    private lateinit var boxAABB: BoxAABB
    private lateinit var worldCenter: Vector3
    private var edgeLength: Float = 0.0f
    private val halfEdgeLength: Float get() = edgeLength * 0.5f

    private val scaleXZ: Float
        get() = max(config.worldScale.x, config.worldScale.z)

    var lod: Int = 0

    fun edgeLength(): Float = edgeLength

    init {
        require(topLeft.x in 0.0..1.0)
        require(topLeft.y in 0.0..1.0)

        this.level = level
        edgeLength = (1.0f / 2.0.pow(level.toDouble())).toFloat()

        calculateWorldCenter()
        calculateBoundingBox()
    }

    fun level(): Int = level
    fun worldCenter(): Vector3 = worldCenter
    fun bounds(): BoxAABB = boxAABB
    fun calculateBoundingBox(): BoxAABB {
        val xzOffset = Vector2(config.heightmap.worldOffset().x, config.heightmap.worldOffset().z)
        val xzScale = Vector2(config.heightmap.worldScale().x, config.heightmap.worldScale().z)

        val minPoint = xzOffset + topLeft * xzScale
        val maxPoint = minPoint + Vector2(edgeLength, edgeLength) * xzScale

        boxAABB = config.heightmap.calculatePatchBounds(
            minPoint, maxPoint
        )

        return boxAABB
    }

    fun calculateWorldCenter() {
        val worldCenterX = config.worldOffset.x + (topLeft.x + halfEdgeLength) * config.worldScale.x
        val worldCenterZ = config.worldOffset.z + (topLeft.y + halfEdgeLength) * config.worldScale.z
        val worldCenterY = config.heightmap.getInterpolatedHeight(
            worldCenterX,
            worldCenterZ
        ) * config.worldScale.y

        worldCenter = Vector3(worldCenterX, worldCenterY, worldCenterZ)
    }

    override fun split() {
        if (!isLeaf) {
            return
        }

        val swKey = buildKey(Child.SW)
        val seKey = buildKey(Child.SE)
        val nwKey = buildKey(Child.NW)
        val neKey = buildKey(Child.NE)

        val sw = quadTreeCache.getOrPut(swKey) {
            CdlodTerrainSection(
                config,
                Vector2(topLeft.x, topLeft.y),
                level + 1,
                lodRanges
            )
        }

        val se = quadTreeCache.getOrPut(seKey) {
            CdlodTerrainSection(
                config,
                Vector2(topLeft.x + halfEdgeLength, topLeft.y),
                level + 1,
                lodRanges
            )
        }

        val nw = QuadTreeTerrainNode.quadTreeCache.getOrPut(nwKey) {
            CdlodTerrainSection(
                config,
                Vector2(topLeft.x, topLeft.y + halfEdgeLength),
                level + 1,
                lodRanges
            )
        }

        val ne = QuadTreeTerrainNode.quadTreeCache.getOrPut(neKey) {
            CdlodTerrainSection(
                config,
                Vector2(topLeft.x + halfEdgeLength, topLeft.y + halfEdgeLength),
                level + 1,
                lodRanges
            )
        }

        ne.resetChildren()
        nw.resetChildren()
        sw.resetChildren()
        se.resetChildren()

        addNode(nw, Child.NW)
        addNode(ne, Child.NE)
        addNode(sw, Child.SW)
        addNode(se, Child.SE)
    }

    override fun merge() {
        children.clear()
    }

    private fun buildKey(child: Child, keyScale: Int = 100): QuadTreeKey {
        return when (child) {
            Child.NW ->
                QuadTreeKey(
                    (topLeft.x * scaleXZ * keyScale).toInt(),
                    ((topLeft.y + halfEdgeLength) * scaleXZ * keyScale).toInt(),
                    level + 1
                )

            Child.NE ->
                QuadTreeKey(
                    ((topLeft.x + halfEdgeLength) * scaleXZ * keyScale).toInt(),
                    ((topLeft.y + halfEdgeLength) * scaleXZ * keyScale).toInt(),
                    level + 1
                )

            Child.SW ->
                QuadTreeKey(
                    (topLeft.x * scaleXZ * keyScale).toInt(),
                    (topLeft.y * scaleXZ * keyScale).toInt(),
                    level + 1
                )

            Child.SE ->
                QuadTreeKey(
                    ((topLeft.x + halfEdgeLength) * scaleXZ * keyScale).toInt(),
                    (topLeft.y * scaleXZ * keyScale).toInt(),
                    level + 1
                )
        }
    }
}