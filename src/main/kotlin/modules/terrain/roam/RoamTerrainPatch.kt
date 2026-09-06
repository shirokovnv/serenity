package modules.terrain.roam

import core.ecs.BaseComponent
import core.management.Disposable
import core.management.Resources
import core.math.*
import core.scene.Transform
import core.scene.camera.Camera
import core.scene.camera.Frustum
import core.scene.camera.PerspectiveCamera
import modules.terrain.heightmap.Heightmap
import modules.terrain.roam.buffers.*
import modules.terrain.roam.tri.*
import modules.terrain.roam.tri.mesh.*
import modules.terrain.roam.tri.refinement.RefinementParams
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max
import kotlin.math.min

class RoamTerrainPatch(
    private val config: RoamTerrainPatchConfig,
    private val heightmap: Heightmap,
    private val transform: Transform,
    private val meshScheme: TriMeshScheme
) : BaseComponent(), Disposable {

    companion object {
        private var sharedVerticesInitialized: Boolean = false
        private val sharedVertices = Array(TriNodeGeometry.treeVertexCapacity) { Vector2() }
    }

    private var pool = TriNodePool()
    private var queue = TriNodeQueue()

    private var nodeA: TriNode
    private var nodeB: TriNode

    private var mesh: TriMesh<Any>? = null

    private lateinit var varianceTree: VarianceTree
    private lateinit var buffer: PatchBufferInterface
    private lateinit var metrics: RoamTerrainPatchMetrics
    private var updateCounter = 0
    private var splitIndex = 0
    private var mergeIndex = 0

    private val numThreads = Runtime.getRuntime().availableProcessors()
    private val workerPool = Executors.newFixedThreadPool(numThreads)

    private val maxCandidates = max(RoamTerrainPatchConfig.MAX_SPLITS, RoamTerrainPatchConfig.MAX_MERGES)
    private val candidateBuffer = Array<TriNode?>(maxCandidates) { null }
    private var candidateCount = AtomicInteger(0)

    private var frustum: Frustum
    private val camera: Camera
        get() = Resources.get<Camera>()!!

    private val canRebuildMeshData: Boolean
        get() {
            return when (meshScheme) {
                TriMeshScheme.MESH_VERTICES -> true
                TriMeshScheme.MESH_INSTANCES -> sharedVerticesInitialized
            }
        }

    init {
        nodeA = pool.allocate(1)
        nodeB = pool.allocate(2)

        nodeA.baseNeighbour = nodeB
        nodeB.baseNeighbour = nodeA

        val rootTriangles = buildRootTriangles()

        varianceTree = VarianceTree(
            heightmap,
            rootTriangles.first,
            rootTriangles.second,
            RefinementParams.MAX_LOD
        )

        nodeA.initialize(
            heightmap,
            pool,
            { canonicalTriBaseVerticesProvider() },
            transform,
            varianceTree
        )

        nodeB.initialize(
            heightmap,
            pool,
            { canonicalTriBaseMirrorVerticesProvider() },
            transform,
            varianceTree
        )

        queue.addSplitTri(nodeA)
        queue.addSplitTri(nodeB)

        nodeA.postSplitCallback = ::postSplitCallback
        nodeA.postMergeCallback = ::postMergeCallback
        nodeB.postSplitCallback = ::postSplitCallback
        nodeB.postMergeCallback = ::postMergeCallback

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

            if (config.parallelOps) {
                triangulateParallel()
            } else {
                triangulate()
            }

            rebuildMesh()
            val endTime = System.nanoTime()
            val elapsedTime = endTime - startTime
            metrics.updateTimeMs = elapsedTime / 1000000
        }
    }

    override fun dispose() {
        buffer.destroy()
        workerPool.shutdown()
    }

    private fun triangulate() {
        val maxSplits = config.maxSplits
        val maxMerges = config.maxMerges
        var splits = 0
        var merges = 0
        val t0 = System.nanoTime()
        if (splitIndex > queue.splitQueueSize() - 1) {
            splitIndex = 0
        }
        val splittingNodes: Array<TriNode?> = queue.getSplitPartition(splitIndex, min(splitIndex + maxSplits, queue.splitQueueSize()))

        val t1 = System.nanoTime()
        metrics.timeToGetSplittingList = (t1 - t0) / 1000000
        val splitLoopStart = System.nanoTime()
        var splitWorkTimeNs: Long = 0

        for (i in splittingNodes.indices) {
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
        if (mergeIndex > queue.mergeQueueSize() - 1) {
            mergeIndex = 0
        }
        val mergingNodes: Array<TriNode?> = queue.getMergePartition(mergeIndex, min(mergeIndex + maxMerges, queue.mergeQueueSize()))

        val t3 = System.nanoTime()
        metrics.timeToGetMergingList = (t3 - t2) / 1000000
        val mergeLoopStart = System.nanoTime()
        var mergeWorkTimeNs: Long = 0

        for (i in mergingNodes.indices) {
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

    private fun triangulateParallel() {
        val maxSplits = config.maxSplits
        val maxMerges = config.maxMerges
        var splits = 0
        var merges = 0
        val t0 = System.nanoTime()
        if (splitIndex > queue.splitQueueSize() - 1) {
            splitIndex = 0
        }
        val splittingNodes: Array<TriNode?> = queue.getSplitPartition(splitIndex, min(splitIndex + maxSplits, queue.splitQueueSize()))

        val t1 = System.nanoTime()
        metrics.timeToGetSplittingList = (t1 - t0) / 1000000
        val splitLoopStart = System.nanoTime()
        var splitWorkTimeNs: Long = 0

        // MARK CANDIDATES FOR SPLIT IN PARALLEL
        var chunkSize = max(1, splittingNodes.size / numThreads)
        workerPool.invokeAll((splittingNodes.indices step chunkSize).map { start ->
            Callable {
                val end = min(start + chunkSize, splittingNodes.size)
                var localCount = 0
                for (i in start..<end) {
                    val node = splittingNodes[i] ?: continue
                    if (node.isClear()) continue
                    if (!config.refinement.shouldSplit(node)) continue
                    if (!frustum.checkSphereInFrustum(node.geometry.boundingSphere)) continue

                    val idx = candidateCount.getAndIncrement()
                    if (idx < maxCandidates) {
                        candidateBuffer[idx] = node
                        localCount++
                    }
                }
                localCount
            }
        }).forEach { it.get() }

        // ACTUAL SPLIT
        for (i in 0..<candidateCount.get()) {
            if (splits >= maxSplits) break
            val node = candidateBuffer[i] ?: continue

            val sStart = System.nanoTime()
            node.split()
            val sEnd = System.nanoTime()
            splitWorkTimeNs += sEnd - sStart

            splits++
        }
        splitIndex += maxSplits
        candidateCount.set(0)

        val splitLoopEnd = System.nanoTime()
        metrics.splitLoopTotalMs = (splitLoopEnd - splitLoopStart) / 1000000
        metrics.splitWorkOnlyCallsMs = splitWorkTimeNs / 1000000
        metrics.numSplitsExecuted = splits

        val t2 = System.nanoTime()
        if (mergeIndex > queue.mergeQueueSize() - 1) {
            mergeIndex = 0
        }
        val mergingNodes: Array<TriNode?> = queue.getMergePartition(mergeIndex, min(mergeIndex + maxMerges, queue.mergeQueueSize()))

        val t3 = System.nanoTime()
        metrics.timeToGetMergingList = (t3 - t2) / 1000000
        val mergeLoopStart = System.nanoTime()
        var mergeWorkTimeNs: Long = 0

        // MARK CANDIDATES FOR MERGE IN PARALLEL
        chunkSize = max(1, mergingNodes.size / numThreads)
        workerPool.invokeAll((mergingNodes.indices step chunkSize).map { start ->
            Callable {
                val end = min(start + chunkSize, mergingNodes.size)
                var localCount = 0
                for (i in start..<end) {
                    val node = mergingNodes[i] ?: continue
                    if (node.isClear()) continue

                    val boundingSphere = Sphere(
                        node.geometry.boundingSphere.center,
                        node.geometry.boundingSphere.radius + config.refinement.params().cullDistThreshold
                    )

                    val shouldMerge = config.refinement.shouldMerge(node)
                            || (!frustum.checkSphereInFrustum(boundingSphere))

                    if (!shouldMerge) {
                        continue
                    }

                    val idx = candidateCount.getAndIncrement()
                    if (idx < maxCandidates) {
                        candidateBuffer[idx] = node
                        localCount++
                    }
                }
                localCount
            }
        }).forEach { it.get() }

        // ACTUAL MERGE
        for (i in 0..<candidateCount.get()) {
            if (merges >= maxMerges) break
            val node = candidateBuffer[i] ?: continue

            val mStart = System.nanoTime()
            node.merge()
            val mEnd = System.nanoTime()
            mergeWorkTimeNs += mEnd - mStart

            merges++
        }
        mergeIndex += maxMerges
        candidateCount.set(0)

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
        val mesh = (mesh!! as TriVertexMesh)
        val vertices = mesh.getMeshData()

        (buffer as PatchVertexBuffer).uploadData(vertices)

        return vertices.size / 6
    }

    private fun rebuildInstances(): Int {
        val mesh = (mesh as TriInstanceMesh)
        val instances = mesh.getMeshData()

        (buffer as PatchIndexBuffer).uploadData(instances)

        return instances.size
    }

    private fun createBuffer(scheme: TriMeshScheme): PatchBufferInterface {
        return when (scheme) {
            TriMeshScheme.MESH_VERTICES -> {
                mesh = TriMeshFactory.create(TriMeshScheme.MESH_VERTICES)
                PatchVertexBuffer()
            }
            TriMeshScheme.MESH_INSTANCES -> {
                mesh = TriMeshFactory.create(TriMeshScheme.MESH_INSTANCES)
                calculateSharedVertices()

                PatchIndexBuffer()
            }
        }
    }

    private fun calculateSharedVertices() {
        if (!sharedVerticesInitialized) {
            nodeA.recursiveSplitToTargetLod(RefinementParams.MAX_LOD)
            nodeB.recursiveSplitToTargetLod(RefinementParams.MAX_LOD)

            nodeA.traverse {
                var iterator = it.index * TriNodeGeometry.vertexPerTriangle

                sharedVertices[iterator++] = it.geometry.localVertices[0]
                sharedVertices[iterator++] = it.geometry.localVertices[1]
                sharedVertices[iterator++] = it.geometry.localVertices[2]
            }

            nodeB.traverse {
                var iterator = it.index * TriNodeGeometry.vertexPerTriangle

                sharedVertices[iterator++] = it.geometry.localVertices[0]
                sharedVertices[iterator++] = it.geometry.localVertices[1]
                sharedVertices[iterator++] = it.geometry.localVertices[2]
            }

            // RESET TO ROOT
            while (queue.mergeQueueSize() > 0) {
                queue.getAllMergeTriangles().forEach { tri ->
                    tri!!.merge()
                }
            }

            val patchSsbo = PatchSsbo(sharedVertices)
            Resources.put(patchSsbo)

            sharedVerticesInitialized = true
        }
    }

    private fun postSplitCallback(tri: TriNode) {
        if (tri.parent != null) {
            queue.removeMergeTri(tri.parent!!)
        }

        queue.addMergeTri(tri)
        queue.removeSplitTri(tri)
        queue.addSplitTri(tri.leftChild!!)
        queue.addSplitTri(tri.rightChild!!)

        if (canRebuildMeshData) {
            mesh!!.addMeshData(tri.leftChild!!.geometry)
            mesh!!.addMeshData(tri.rightChild!!.geometry)
            mesh!!.releaseMeshData(tri.geometry)
        }
    }

    private fun postMergeCallback(tri: TriNode) {
        queue.removeSplitTri(tri.leftChild!!)
        queue.removeSplitTri(tri.rightChild!!)
        queue.removeMergeTri(tri)
        queue.addSplitTri(tri)

        if (tri.parent != null) {
            queue.addMergeTri(tri.parent!!)
        }

        if (canRebuildMeshData) {
            mesh!!.releaseMeshData(tri.leftChild!!.geometry)
            mesh!!.releaseMeshData(tri.rightChild!!.geometry)
            mesh!!.addMeshData(tri.geometry)
        }
    }

    private fun buildRootTriangles(): Pair<Triangle, Triangle> {
        val rootAVertices = canonicalTriBaseVerticesProvider().map {triLocalVertex ->
            val wsX = triLocalVertex.x * transform.scale().x
            val wsZ = triLocalVertex.y * transform.scale().z

            val height = heightmap.getInterpolatedHeight(wsX, wsZ)
            val localPosition = Quaternion(triLocalVertex.x, height, triLocalVertex.y, 1.0f)
            val worldPosition = (transform.matrix() * localPosition).xyz()

            worldPosition
        }.toList()

        val rootBVertices = canonicalTriBaseMirrorVerticesProvider().map { triLocalVertex ->
            val wsX = triLocalVertex.x * transform.scale().x
            val wsZ = triLocalVertex.y * transform.scale().z

            val height = heightmap.getInterpolatedHeight(wsX, wsZ)
            val localPosition = Quaternion(triLocalVertex.x, height, triLocalVertex.y, 1.0f)
            val worldPosition = (transform.matrix() * localPosition).xyz()

            worldPosition
        }.toList()

        return Pair(
            Triangle(
                rootAVertices[0],
                rootAVertices[1],
                rootAVertices[2]
            ),
            Triangle(
                rootBVertices[0],
                rootBVertices[1],
                rootBVertices[2]
            )
        )
    }
}