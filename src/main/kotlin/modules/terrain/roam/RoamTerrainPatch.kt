package modules.terrain.roam

import core.ecs.BaseComponent
import core.management.Disposable
import core.management.Resources
import core.math.Sphere
import core.math.Vector2
import core.math.Vector3
import core.scene.Transform
import core.scene.camera.Camera
import core.scene.camera.Frustum
import core.scene.camera.PerspectiveCamera
import modules.terrain.heightmap.Heightmap
import modules.terrain.roam.buffers.*
import modules.terrain.roam.tri.*
import modules.terrain.roam.tri.mesh.TriInstanceMesh
import modules.terrain.roam.tri.mesh.TriMeshFactory
import modules.terrain.roam.tri.mesh.TriMeshScheme
import modules.terrain.roam.tri.mesh.TriVertexMesh
import modules.terrain.roam.tri.refinement.RefinementParams
import kotlin.math.min

class RoamTerrainPatch(
    private val config: RoamTerrainPatchConfig,
    private val heightmap: Heightmap,
    private val transform: Transform,
    private val meshScheme: TriMeshScheme
) : BaseComponent(), Disposable {

    companion object {
        private var sharedVertexBufferCreated: Boolean = false
        private val maxTriangles = TriNodeGeometry.maxLeafTriangles

        private val staticBoundingSphere = Sphere(Vector3(0f), 0f)
    }

    private var pool = TriNodePool()
    private var queue = TriNodeQueue()

    private var nodeA: TriNode
    private var nodeB: TriNode

    private lateinit var buffer: PatchBufferInterface
    private lateinit var metrics: RoamTerrainPatchMetrics
    private var updateCounter = 0
    private var splitIndex = 0
    private var mergeIndex = 0

    private lateinit var frustum: Frustum
    private val camera: Camera
        get() = Resources.get<Camera>()!!

    init {
        nodeA = pool.allocate(1)
        nodeB = pool.allocate(2)

        nodeA.baseNeighbour = nodeB
        nodeB.baseNeighbour = nodeA

        nodeA.initialize(
            heightmap,
            pool,
            { canonicalTriBaseVerticesProvider() },
            transform,
            queue
        )

        nodeB.initialize(
            heightmap,
            pool,
            { canonicalTriBaseMirrorVerticesProvider() },
            transform,
            queue
        )

        nodeA.recursiveSplitToTargetLod(0)
        nodeB.recursiveSplitToTargetLod(0)

        queue.addSplitTri(nodeA)
        queue.addSplitTri(nodeB)

        buffer = createBuffer(meshScheme)
        frustum = Frustum(camera as PerspectiveCamera)
        metrics = RoamTerrainPatchMetrics()
    }

    fun meshScheme(): TriMeshScheme = meshScheme
    fun heightmap(): Heightmap = heightmap
    fun buffer(): PatchBufferInterface = buffer
    fun metrics(): RoamTerrainPatchMetrics = metrics
    fun baseTriangles(): Pair<TriNode, TriNode> = Pair(nodeA, nodeB)

    fun update() {
        updateCounter++
        if (updateCounter % config.perFrameUpdate == 0) {
            val startTime = System.nanoTime()
            frustum.recalculatePlanes()
            frustum.recalculateSearchVolume()
            triangulate()
            rebuildMesh()
            val endTime = System.nanoTime()
            val elapsedTime = endTime - startTime
            metrics.updateTimeMs = elapsedTime / 1000000
        }
    }

    override fun dispose() {
        buffer.destroy()
    }

    private fun triangulate() {
        val maxSplits = config.maxSplits
        val maxMerges = config.maxMerges
        var splits = 0
        var merges = 0
        val t0 = System.nanoTime()
        val splittingNodes: Array<TriNode?> = queue.getAllSplitTriangles()
        if (splitIndex > splittingNodes.size - 1) {
            splitIndex = 0
        }

        val t1 = System.nanoTime()
        metrics.timeToGetSplittingList = (t1 - t0) / 1000000
        val splitLoopStart = System.nanoTime()
        var splitWorkTimeNs: Long = 0

        for (i in splitIndex..<min(splitIndex + maxSplits, splittingNodes.size)) {
            val splittingNode = splittingNodes[i]

            if (splits >= maxSplits) {
                break
            }

            if (splittingNode!!.isClear()) {
                continue
            }

            if (!config.refinement.shouldSplit(
                    splittingNode
                )
            ) {
                continue
            }

            if (!frustum.checkSphereInFrustum(splittingNode.geometry.boundingSphere)) continue

            val sStart = System.nanoTime()
            splittingNode.split()
            val sEnd = System.nanoTime()
            splitWorkTimeNs += sEnd - sStart

            splits++
        }
        splitIndex += maxSplits

        val splitLoopEnd = System.nanoTime()
        metrics.splitLoopTotalMs = (splitLoopEnd - splitLoopStart) / 1000000
        metrics.splitWorkOnlyCallsMs = splitWorkTimeNs / 1000000
        metrics.numSplitsExecuted = splits

        val t2 = System.nanoTime()
        val mergingNodes: Array<TriNode?> = queue.getAllMergeTriangles()
        if (mergeIndex > mergingNodes.size - 1) {
            mergeIndex = 0
        }

        val t3 = System.nanoTime()
        metrics.timeToGetMergingList = (t3 - t2) / 1000000
        val mergeLoopStart = System.nanoTime()
        var mergeWorkTimeNs: Long = 0

        for (i in mergeIndex..<min(mergeIndex + maxMerges, mergingNodes.size)) {
            val mergingNode = mergingNodes[i]

            if (merges >= maxMerges) {
                break
            }

            if (mergingNode!!.isClear()) {
                continue
            }

            val boundingSphere = Sphere(
                mergingNode.geometry.boundingSphere.center,
                mergingNode.geometry.boundingSphere.radius + config.refinement.params().cullDistThreshold
            )

            val shouldMerge =
                config.refinement.shouldMerge(
                    mergingNode
                ) || (!frustum.checkSphereInFrustum(boundingSphere))

            if (!shouldMerge) {
                continue
            }

            val mStart = System.nanoTime()
            mergingNode.merge()
            val mEnd = System.nanoTime()
            mergeWorkTimeNs += mEnd - mStart

            merges++
        }
        mergeIndex += maxMerges

        val mergeLoopEnd = System.nanoTime()
        metrics.mergeLoopTotalMs = (mergeLoopEnd - mergeLoopStart) / 1000000
        metrics.mergeWorkOnlyCallsMs = mergeWorkTimeNs / 1000000
        metrics.numMergesExecuted = merges
        val total = System.nanoTime()
        metrics.triangulationTimeMs = (total - t0) / 1000000
    }

    private fun rebuildMesh() {
        val startTime = System.nanoTime()

        val numTriangles = when (buffer) {
            is PatchVertexBuffer -> rebuildVertices()
            is PatchIndexBuffer -> rebuildInstances()
            else -> 0
        }

        val endTime = System.nanoTime()
        val elapsedTime = endTime - startTime
        metrics.meshRebuildTime = elapsedTime / 1000000
        metrics.numTriangles = numTriangles
    }

    private fun rebuildVertices(): Int {
        val mesh = (TriNodeGeometry.mesh!! as TriVertexMesh)
        val vertices = mesh.getMeshData()

        (buffer as PatchVertexBuffer).uploadData(vertices)

        return vertices.size / 6
    }

    private fun rebuildInstances(): Int {
        val mesh = (TriNodeGeometry.mesh as TriInstanceMesh)
        val instances = mesh.getMeshData()

        (buffer as PatchIndexBuffer).uploadData(instances)

        return instances.size
    }

    private fun createBuffer(scheme: TriMeshScheme): PatchBufferInterface {
        return when (scheme) {
            TriMeshScheme.MESH_VERTICES -> {
                TriNodeGeometry.mesh = TriMeshFactory.create(TriMeshScheme.MESH_VERTICES)
                PatchVertexBuffer()
            }
            TriMeshScheme.MESH_INSTANCES -> {
                val buffer = PatchIndexBuffer()

                nodeA.recursiveSplitToTargetLod(RefinementParams.MAX_LOD)
                nodeB.recursiveSplitToTargetLod(RefinementParams.MAX_LOD)

                if (!sharedVertexBufferCreated) {
                    val patchSsbo = PatchSsbo(TriNodeGeometry.treeVertices)
                    Resources.put(patchSsbo)
                    sharedVertexBufferCreated = true
                }

                nodeA.recursiveMergeToTargetLod(RefinementParams.MIN_LOD)
                nodeB.recursiveMergeToTargetLod(RefinementParams.MIN_LOD)

                TriNodeGeometry.mesh = TriMeshFactory.create(TriMeshScheme.MESH_INSTANCES)

                queue.getAllSplitTriangles().forEach { tri ->
                    tri!!.geometry.collectMeshData()
                }

                buffer
            }
        }
    }
}