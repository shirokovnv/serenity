package core.scene.navigation

import core.math.IntersectionDetector
import core.math.Rect2d
import core.math.Vector2
import core.math.extensions.saturate
import core.scene.Object
import core.scene.navigation.agents.NavMeshAgent
import core.scene.navigation.obstacles.NavMeshObstacle
import core.scene.spatial.SpatialPartitioningInterface
import core.scene.volumes.BoxAABB
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.math.floor

class NavGrid(private val bounds: Rect2d, private val dimensions: Vector2) : SpatialPartitioningInterface {
    private val cells: ConcurrentHashMap<String, ThreadSafeHashSet<Object>> = ConcurrentHashMap()
    private val objectIndexCache: ConcurrentHashMap<Object, Array<Vector2>> = ConcurrentHashMap()

    override fun insert(obj: Object): Boolean {
        val objRect = getObjectBounds(obj)

        val i1 = getCellIndex(objRect.min)
        val i2 = getCellIndex(objRect.max)

        var isAdded = true
        for (xi in i1.x.toInt()..i2.x.toInt()) {
            for (yi in i1.y.toInt()..i2.y.toInt()) {
                val k = getKey(xi, yi)
                cells.computeIfAbsent(k) { ThreadSafeHashSet() }
                isAdded = isAdded and (cells[k]?.add(obj) ?: false)
            }
        }

        objectIndexCache[obj] = arrayOf(i1, i2)

        return isAdded
    }

    override fun remove(obj: Object): Boolean {
        val objectIndices = objectIndexCache.remove(obj) ?: return false
        val (i1, i2) = objectIndices

        var isRemoved = true
        for (xi in i1.x.toInt()..i2.x.toInt()) {
            for (yi in i1.y.toInt()..i2.y.toInt()) {
                val k = getKey(xi, yi)
                cells.computeIfAbsent(k) { ThreadSafeHashSet() }
                isRemoved = isRemoved and (cells[k]?.remove(obj) ?: false)
            }
        }

        return isRemoved
    }

    override fun countObjects(): Int {
        return objectIndexCache.keys.size
    }

    override fun buildSearchResults(searchVolume: BoxAABB): List<Object> {
        val searchRect = searchVolume.toRect2d()

        val i1 = getCellIndex(searchRect.min)
        val i2 = getCellIndex(searchRect.max)

        val objects = HashSet<Object>()

        for (xi in i1.x.toInt()..i2.x.toInt()) {
            for (yi in i1.y.toInt()..i2.y.toInt()) {
                val k = getKey(xi, yi)
                cells.computeIfAbsent(k) { ThreadSafeHashSet() }
                objects.addAll(cells[k]?.filter { obj ->
                    IntersectionDetector.intersects(getObjectBounds(obj), searchRect)
                } ?: emptySet())
            }
        }

        return objects.toList()
    }

    fun getCellIndices(objRect: Rect2d): IntArray {
        val i1 = getCellIndex(objRect.min)
        val i2 = getCellIndex(objRect.max)

        return arrayOf(
            i1.x.toInt(),
            i1.y.toInt(),
            i2.x.toInt(),
            i2.y.toInt()
        ).toIntArray()
    }

    private fun getCellIndex(position: Vector2): Vector2 {
        val unionX = ((position.x - bounds.min.x) / (bounds.max.x - bounds.min.x)).saturate()
        val unionY = ((position.y - bounds.min.y) / (bounds.max.y - bounds.min.y)).saturate()

        val xIndex = floor(unionX * (dimensions.x - 1f))
        val yIndex = floor(unionY * (dimensions.y - 1f))

        return Vector2(xIndex, yIndex)
    }

    private fun getKey(xIndex: Int, yIndex: Int): String {
        return "$xIndex.$yIndex"
    }

    private fun getObjectBounds(obj: Object): Rect2d {
        return when (obj) {
            is NavMeshObstacle -> obj.getObstacleBounds().toRect2d()
            is NavMeshAgent -> obj.getAgentBounds().toRect2d()
            else -> {
                obj.bounds().toRect2d()
            }
        }
    }
}

class ThreadSafeHashSet<T : Any> {
    private val map = ConcurrentHashMap<T, Boolean>()
    private val lock = ReentrantReadWriteLock()

    fun add(element: T): Boolean {
        return lock.write { map.put(element, true) == null }
    }

    fun remove(element: T): Boolean {
        return lock.write { map.remove(element) != null }
    }

    fun contains(element: T): Boolean {
        return lock.read { map.containsKey(element) }
    }

    fun filter(predicate: (T) -> Boolean): List<T> {
        return lock.read {
            map.keys.filter(predicate).toList()
        }
    }

    val size: Int
        get() = lock.read { map.size }
}