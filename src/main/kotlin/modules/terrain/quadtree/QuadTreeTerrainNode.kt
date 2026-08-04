package modules.terrain.quadtree

import core.math.Quaternion
import core.math.Vector2
import core.math.Vector3
import core.math.helpers.distance
import core.scene.camera.Camera
import core.scene.camera.Frustum
import core.scene.spatial.DynamicQuadTreeNode
import kotlin.math.pow
import kotlin.math.sqrt

class QuadTreeTerrainNode(
    private val config: QuadTreeTerrainConfig,
    private val topLeft: Vector2,
    level: Int,
    private val lodConfig: QuadTreeLoDConfig
) : DynamicQuadTreeNode() {

    companion object {
        val quadTreeCache = QuadTreeCache(1000, 30000L)

        private var lodRanges: FloatArray? = null

        fun calculateLodRanges(maxLod: Int, scaleXZ: Float, distanceMultiplier: Float) {
            lodRanges = FloatArray(maxLod + 1)

            val minQuadDiagonal = (sqrt(2.0) * (1.0f / 2.0.pow((maxLod - 1).toDouble()))).toFloat()
            val minLodDistance = minQuadDiagonal * scaleXZ * distanceMultiplier

            for (i in 0..<maxLod + 1) {
                val lodRange = minLodDistance * 2.0.pow(i.toDouble()).toFloat()
                lodRanges!![maxLod - i] = lodRange
            }
        }
    }

    private var edgeLength: Float = 0.0f
    private val halfEdgeLength: Float get() = edgeLength * 0.5f
    private var patch: QuadTreeTerrainPatch

    private var worldCenter: Vector3
    private var p0: Vector3
    private var p1: Vector3
    private var p2: Vector3
    private var p3: Vector3

    fun worldCenter(): Vector3 = worldCenter
    fun patch(): QuadTreeTerrainPatch = patch

    init {
        this.level = level
        edgeLength = (1.0f / 2.0.pow(level.toDouble())).toFloat()

        require(topLeft.x in 0.0..1.0)
        require(topLeft.y in 0.0..1.0)

        val worldCenterX = config.worldOffset.x + (topLeft.x + halfEdgeLength) * config.worldScale.x
        val worldCenterZ = config.worldOffset.z + (topLeft.y + halfEdgeLength) * config.worldScale.z
        val worldCenterY = config.heightmap.getInterpolatedHeight(
            worldCenterX,
            worldCenterZ
        ) * config.worldScale.y

        worldCenter = Vector3(worldCenterX, worldCenterY, worldCenterZ)
        p0 = config.worldOffset + Vector3(
            topLeft.x, 0.0f, topLeft.y
        ) * config.worldScale
        p1 = config.worldOffset + Vector3(
            topLeft.x + edgeLength, 0.0f, topLeft.y
        ) * config.worldScale
        p2 = config.worldOffset + Vector3(
            topLeft.x + edgeLength, 0.0f, topLeft.y + edgeLength
        ) * config.worldScale
        p3 = config.worldOffset + Vector3(
            topLeft.x, 0.0f, topLeft.y + edgeLength
        ) * config.worldScale

        patch = QuadTreeTerrainPatch(
            config.heightmap,
            topLeft,
            edgeLength
        )

        if (lodRanges == null) {
            calculateLodRanges(lodConfig.maxDepth, config.getXZScale(), lodConfig.distanceMultiplier)
        }
    }

    private fun calculateNeighbourLodVector(): Quaternion {

        val nW = getNeighborOfGreaterOrEqualSize(Direction.W) as? QuadTreeTerrainNode
        val nE = getNeighborOfGreaterOrEqualSize(Direction.E) as? QuadTreeTerrainNode
        val nS = getNeighborOfGreaterOrEqualSize(Direction.S) as? QuadTreeTerrainNode
        val nN = getNeighborOfGreaterOrEqualSize(Direction.N) as? QuadTreeTerrainNode

        val lodAB = if (nW == null || nW.isLeaf) 1 else 2
        val lodBC = if (nN == null || nN.isLeaf) 1 else 2
        val lodCD = if (nE == null || nE.isLeaf) 1 else 2
        val lodDA = if (nS == null || nS.isLeaf) 1 else 2

        val finalAB = lodAB * lodConfig.tessFactor
        val finalBC = lodBC * lodConfig.tessFactor
        val finalCD = lodCD * lodConfig.tessFactor
        val finalDA = lodDA * lodConfig.tessFactor

        return Quaternion(finalAB.toFloat(), finalBC.toFloat(), finalCD.toFloat(), finalDA.toFloat())
    }

    fun recursiveCollectInstanceData(): List<QuadTreeInstanceData> {
        val result = mutableListOf<QuadTreeInstanceData>()
        val stack = ArrayDeque<QuadTreeTerrainNode>()
        stack.addFirst(this)

        while (stack.isNotEmpty()) {
            val node = stack.removeFirst()

            if (node.isLeaf) {
                result.add(
                    QuadTreeInstanceData(
                        node.topLeft,
                        Vector3(node.edgeLength, 1.0f, node.edgeLength),
                        node.calculateNeighbourLodVector(),
                        Vector3(node.patch.bounds().shape().min),
                        Vector3(node.patch.bounds().shape().max),
                    )
                )
            } else {
                for (child in node.children) {
                    stack.addFirst(child as QuadTreeTerrainNode)
                }
            }
        }

        return result
    }

    fun recursiveUpdate(camera: Camera, frustum: Frustum) {
        if (!frustum.checkRect3dInFrustum(patch.bounds().shape())) {
            merge()
            return
        }

        val from = camera.position()
        val to = Vector3(worldCenter.x, 0.0f, worldCenter.z)

        val distance = distance(from, to)

        if (level < lodConfig.maxDepth && distance <= lodRanges!![level]) {
            split()
        } else if (distance > lodRanges!![level]) {
            merge()
        }

        children.forEach { child ->
            (child as QuadTreeTerrainNode).recursiveUpdate(
                camera,
                frustum
            )
        }
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
            QuadTreeTerrainNode(
                config,
                Vector2(topLeft.x, topLeft.y),
                level + 1,
                lodConfig
            )
        }

        val se = quadTreeCache.getOrPut(seKey) {
            QuadTreeTerrainNode(
                config,
                Vector2(topLeft.x + halfEdgeLength, topLeft.y),
                level + 1,
                lodConfig
            )
        }

        val nw = quadTreeCache.getOrPut(nwKey) {
            QuadTreeTerrainNode(
                config,
                Vector2(topLeft.x, topLeft.y + halfEdgeLength),
                level + 1,
                lodConfig
            )
        }

        val ne = quadTreeCache.getOrPut(neKey) {
            QuadTreeTerrainNode(
                config,
                Vector2(topLeft.x + halfEdgeLength, topLeft.y + halfEdgeLength),
                level + 1,
                lodConfig
            )
        }

        ne.children.clear()
        nw.children.clear()
        sw.children.clear()
        se.children.clear()

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
                    (topLeft.x * config.getXZScale() * keyScale).toInt(),
                    ((topLeft.y + halfEdgeLength) * config.getXZScale() * keyScale).toInt(),
                    level + 1
                )
            Child.NE ->
                QuadTreeKey(
                    ((topLeft.x + halfEdgeLength) * config.getXZScale() * keyScale).toInt(),
                    ((topLeft.y + halfEdgeLength) * config.getXZScale() * keyScale).toInt(),
                    level + 1
                )
            Child.SW ->
                QuadTreeKey(
                    (topLeft.x * config.getXZScale() * keyScale).toInt(),
                    (topLeft.y * config.getXZScale() * keyScale).toInt(),
                    level + 1
                )
            Child.SE ->
                QuadTreeKey(
                    ((topLeft.x + halfEdgeLength) * config.getXZScale() * keyScale).toInt(),
                    (topLeft.y * config.getXZScale() * keyScale).toInt(),
                    level + 1
                )
        }
    }
}