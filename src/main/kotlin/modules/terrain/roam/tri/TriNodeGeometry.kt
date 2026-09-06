package modules.terrain.roam.tri

import core.math.*
import core.scene.Transform
import modules.terrain.heightmap.Heightmap
import modules.terrain.roam.tri.refinement.RefinementParams
import kotlin.math.*
import kotlin.properties.Delegates

class TriNodeGeometry(
    val node: TriNode,
    val heightmap: Heightmap,
    val worldTransform: Transform,
    val localVerticesProvider: TriLocalVerticesProvider,
    val varianceTree: VarianceTree
) {
    companion object {
        val vertexPerTriangle = 3
        val maxLeafTriangles = (2.0f.pow(RefinementParams.MAX_LOD + 1)).toInt()
        val treeVertexCapacity = (2.0f.pow(RefinementParams.MAX_LOD + 2) - 1).toInt() * vertexPerTriangle
    }

    var variance: Float = 0.0f

    lateinit var localVertices: Array<Vector2>
    lateinit var worldBaseCenter: Vector3
    lateinit var worldVertices: Array<Vector3>
    var triSize by Delegates.notNull<Float>()
    lateinit var boundingBox: Rect3d
    lateinit var boundingSphere: Sphere

    private var initialized: Boolean = false
    var meshIndex: Int = -1

    val center: Vector3
        get() = (worldVertices[0] + worldVertices[1] + worldVertices[2]) / 3.0f

    init {
        initialize()
    }

    fun initialize() {
        if (initialized) {
            return
        }

        calculateLocalVertices()
        calculateWorldVertices()
        calculateTriSize()
        calculateWorldBaseCenter()
        calculateVariance()
        calculateBoundingBox()
        calculateBoundingSphere()

        initialized = true
    }

    fun calculateLocalVertices() {
        localVertices = localVerticesProvider()
        require(localVertices.size == 3)
    }

    fun calculateWorldVertices() {
        worldVertices = Array(3) { Vector3(0f) }
        for (i in 0..<3) {
            val wsX = localVertices[i].x * worldTransform.scale().x
            val wsZ = localVertices[i].y * worldTransform.scale().z

            val height = heightmap.getInterpolatedHeight(wsX, wsZ)

            val localPosition = Quaternion(localVertices[i].x, height, localVertices[i].y, 1.0f)
            val worldPosition = worldTransform.matrix() * localPosition

            worldVertices[i] = worldPosition.xyz()
        }
    }

    fun calculateTriSize() {
        triSize = (worldVertices[2] - worldVertices[1]).length()
    }

    fun calculateWorldBaseCenter() {
        worldBaseCenter = (worldVertices[1] + worldVertices[2]) * 0.5f
    }

    fun calculateBoundingBox() {
        val minX = min(min(worldVertices[0].x, worldVertices[1].x), worldVertices[2].x)
        val minY = min(min(worldVertices[0].y, worldVertices[1].y), worldVertices[2].y)
        val minZ = min(min(worldVertices[0].z, worldVertices[1].z), worldVertices[2].z)

        val maxX = max(max(worldVertices[0].x, worldVertices[1].x), worldVertices[2].x)
        val maxY = max(max(worldVertices[0].y, worldVertices[1].y), worldVertices[2].y)
        val maxZ = max(max(worldVertices[0].z, worldVertices[1].z), worldVertices[2].z)

        val bMin = Vector3(minX, minY, minZ)
        val bMax = Vector3(maxX, maxY, maxZ)

        boundingBox = Rect3d(bMin, bMax)
    }

    fun calculateBoundingSphere() {
        val dist0 = (worldVertices[0] - center).length()
        val dist1 = (worldVertices[1] - center).length()
        val dist2 = (worldVertices[2] - center).length()

        val radius = max(dist0, max(dist1, dist2))

        boundingSphere = Sphere(Vector3(center), radius)
    }

    fun calculateVariance() {
        variance = varianceTree.getVariance(node)
    }
}