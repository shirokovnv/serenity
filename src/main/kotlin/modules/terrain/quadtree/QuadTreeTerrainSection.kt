package modules.terrain.quadtree

import core.math.Quaternion
import core.math.Vector2
import core.math.Vector3
import core.math.helpers.distance
import core.scene.camera.Camera
import core.scene.camera.Frustum
import core.scene.spatial.QuadTreeCache
import core.scene.spatial.QuadTreeKey
import core.scene.spatial.QuadTreeNode
import core.scene.volumes.BoxAABB
import kotlin.math.pow

class QuadTreeTerrainSection(
    private val config: QuadTreeTerrainConfig,
    private val topLeft: Vector2,
    level: Int,
    private val lodConfig: QuadTreeLoDConfig,
    private val lodRanges: FloatArray,
    private val quadTreeCache: QuadTreeCache
) : QuadTreeNode() {

    private var edgeLength: Float = 0.0f
    private val halfEdgeLength: Float get() = edgeLength * 0.5f
    private lateinit var bounds: BoxAABB
    private lateinit var worldCenter: Vector3

    init {
        this.level = level
        edgeLength = (1.0f / 2.0.pow(level.toDouble())).toFloat()

        require(topLeft.x in 0.0..1.0)
        require(topLeft.y in 0.0..1.0)

        calculateWorldCenter()
        calculateBounds()
    }

    fun worldCenter(): Vector3 = worldCenter
    fun bounds(): BoxAABB = bounds

    fun calculateBounds(): BoxAABB {
        val xzOffset = Vector2(config.heightmap.worldOffset().x, config.heightmap.worldOffset().z)
        val xzScale = Vector2(config.heightmap.worldScale().x, config.heightmap.worldScale().z)

        val minPoint = xzOffset + topLeft * xzScale
        val maxPoint = minPoint + Vector2(edgeLength, edgeLength) * xzScale

        bounds = config.heightmap.calculatePatchBounds(
            minPoint, maxPoint
        )

        return bounds
    }

    private fun calculateWorldCenter(): Vector3 {
        val worldCenterX = config.worldOffset.x + (topLeft.x + halfEdgeLength) * config.worldScale.x
        val worldCenterZ = config.worldOffset.z + (topLeft.y + halfEdgeLength) * config.worldScale.z
        val worldCenterY = config.heightmap.getInterpolatedHeight(
            worldCenterX,
            worldCenterZ
        ) * config.worldScale.y

        worldCenter = Vector3(worldCenterX, worldCenterY, worldCenterZ)

        return worldCenter
    }

    private fun calculateNeighbourLodVector(): Quaternion {

        val nW = getNeighborOfGreaterOrEqualSize(Direction.W) as? QuadTreeTerrainSection
        val nE = getNeighborOfGreaterOrEqualSize(Direction.E) as? QuadTreeTerrainSection
        val nS = getNeighborOfGreaterOrEqualSize(Direction.S) as? QuadTreeTerrainSection
        val nN = getNeighborOfGreaterOrEqualSize(Direction.N) as? QuadTreeTerrainSection

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
        val stack = ArrayDeque<QuadTreeTerrainSection>()
        stack.addFirst(this)

        while (stack.isNotEmpty()) {
            val node = stack.removeFirst()

            if (node.isLeaf) {
                result.add(
                    QuadTreeInstanceData(
                        node.topLeft,
                        Vector3(node.edgeLength, 1.0f, node.edgeLength),
                        node.calculateNeighbourLodVector(),
                        Vector3(node.bounds().shape().min),
                        Vector3(node.bounds().shape().max),
                    )
                )
            } else {
                for (child in node.children) {
                    stack.addFirst(child as QuadTreeTerrainSection)
                }
            }
        }

        return result
    }

    fun recursiveUpdate(camera: Camera, frustum: Frustum) {
        if (!frustum.checkRect3dInFrustum(bounds().shape())) {
            merge()
            return
        }

        val from = camera.position()
        val to = Vector3(worldCenter.x, 0.0f, worldCenter.z)

        val distance = distance(from, to)

        if (level < lodConfig.maxDepth && distance <= lodRanges[level]) {
            split()
        } else if (distance > lodRanges[level]) {
            merge()
        }

        children.forEach { child ->
            (child as QuadTreeTerrainSection).recursiveUpdate(
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
            QuadTreeTerrainSection(
                config,
                Vector2(topLeft.x, topLeft.y),
                level + 1,
                lodConfig,
                lodRanges,
                quadTreeCache
            )
        }

        val se = quadTreeCache.getOrPut(seKey) {
            QuadTreeTerrainSection(
                config,
                Vector2(topLeft.x + halfEdgeLength, topLeft.y),
                level + 1,
                lodConfig,
                lodRanges,
                quadTreeCache
            )
        }

        val nw = quadTreeCache.getOrPut(nwKey) {
            QuadTreeTerrainSection(
                config,
                Vector2(topLeft.x, topLeft.y + halfEdgeLength),
                level + 1,
                lodConfig,
                lodRanges,
                quadTreeCache
            )
        }

        val ne = quadTreeCache.getOrPut(neKey) {
            QuadTreeTerrainSection(
                config,
                Vector2(topLeft.x + halfEdgeLength, topLeft.y + halfEdgeLength),
                level + 1,
                lodConfig,
                lodRanges,
                quadTreeCache
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