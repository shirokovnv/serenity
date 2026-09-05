package modules.terrain.quadtree

import core.math.Vector2
import core.scene.camera.Camera
import core.scene.camera.Frustum
import core.scene.spatial.QuadTreeCache
import kotlin.math.pow
import kotlin.math.sqrt

class QuadTreeTerrainSystem(
    private val config: QuadTreeTerrainConfig,
    private val lodConfig: QuadTreeLoDConfig
    ) {

    private var root: QuadTreeTerrainSection
    private var lodRanges: FloatArray = FloatArray(lodConfig.maxDepth + 1)
    private val quadTreeCache: QuadTreeCache = QuadTreeCache(1000, 30000L)

    private var lastCleanupTimeMs = System.currentTimeMillis()
    private val cleanupIntervalMs = 10_000L

    init {
        calculateLodRanges()
        root = QuadTreeTerrainSection(
            config,
            Vector2(0.0f, 0.0f),
            0,
            lodConfig,
            lodRanges,
            quadTreeCache
        )
    }

    fun calculateLodRanges() {
        val minQuadDiagonal = (sqrt(2.0) * (1.0f / 2.0.pow((lodConfig.maxDepth - 1).toDouble()))).toFloat()
        val minLodDistance = minQuadDiagonal * config.getXZScale() * lodConfig.distanceMultiplier

        for (i in 0..<lodConfig.maxDepth + 1) {
            val lodRange = minLodDistance * 2.0.pow(i.toDouble()).toFloat()
            lodRanges[lodConfig.maxDepth - i] = lodRange
        }
    }

    fun root() = root
    fun cacheSize() = quadTreeCache.count()

    fun update(camera: Camera, frustum: Frustum) {
        val now = System.currentTimeMillis()
        if (now - lastCleanupTimeMs >= cleanupIntervalMs) {
            println("HITS: " + quadTreeCache.hits())
            println("ALLOC: " + quadTreeCache.allocations())
            println("CLEANUP")
            quadTreeCache.cleanupExpired()
            lastCleanupTimeMs = now
        }
        root.recursiveUpdate(camera, frustum)
    }

    fun prepareRenderData(): List<QuadTreeInstanceData> {
        return root.recursiveCollectInstanceData()
    }

    fun clear() {
        root.clear()
    }
}