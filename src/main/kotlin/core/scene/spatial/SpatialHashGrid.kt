package core.scene.spatial

import core.math.IntersectionDetector
import core.math.Rect3d
import core.math.Vector3
import core.math.extensions.saturate
import core.scene.BoxAABB
import core.scene.Object
import kotlin.math.floor

class SpatialHashGrid(private val bounds: Rect3d, private val dimensions: Vector3) : SpatialPartitioningInterface {
    private val cells: HashMap<String, HashSet<Object>> = HashMap()

    override fun insert(obj: Object): Boolean {
        val objRect = obj.getComponent<BoxAABB>()!!.shape()

        val i1 = getCellIndex(objRect.min)
        val i2 = getCellIndex(objRect.max)

        for (xi in i1.x.toInt()..i2.x.toInt()) {
            for (yi in i1.y.toInt()..i2.y.toInt()) {
                for (zi in i1.z.toInt()..i2.z.toInt()) {
                    val k = getKey(xi, yi, zi)
                    cells.computeIfAbsent(k) { HashSet() }
                    obj.setSpatialIndices(arrayOf(i1, i2))
                    return cells[k]?.add(obj) ?: false
                }
            }
        }

        return false
    }

    override fun remove(obj: Object): Boolean {
        val (i1, i2) = obj.spatialIndices()

        for (xi in i1.x.toInt()..i2.x.toInt()) {
            for (yi in i1.y.toInt()..i2.y.toInt()) {
                for (zi in i1.z.toInt()..i2.z.toInt()) {
                    val k = getKey(xi, yi, zi)
                    cells.computeIfAbsent(k) { HashSet() }
                    val removed = cells[k]?.remove(obj) ?: false
                    if (removed) {
                        obj.setSpatialIndices(arrayOf(Vector3(-1f), Vector3(-1f)))
                    }
                    return removed
                }
            }
        }

        return false
    }

    override fun countObjects(): Int {
        return cells.entries.sumOf { it.value.size }
    }

    override fun buildSearchResults(searchVolume: BoxAABB): List<Object> {
        val searchRect = searchVolume.shape()

        val i1 = getCellIndex(searchRect.min)
        val i2 = getCellIndex(searchRect.max)

        val objects = HashSet<Object>()

        for (xi in i1.x.toInt()..i2.x.toInt()) {
            for (yi in i1.y.toInt()..i2.y.toInt()) {
                for (zi in i1.z.toInt()..i2.z.toInt()) {
                    val k = getKey(xi, yi, zi)
                    cells.computeIfAbsent(k) { HashSet() }
                    objects.addAll(cells[k]?.filter {obj ->
                        IntersectionDetector.intersects(obj.getComponent<BoxAABB>()!!.shape(), searchRect)
                    } ?: emptySet())
                }
            }
        }

        return objects.toList()
    }

    private fun getCellIndex(position: Vector3): Vector3 {
        val unionX = ((position.x - bounds.min.x) / (bounds.max.x - bounds.min.x)).saturate()
        val unionY = ((position.y - bounds.min.y) / (bounds.max.y - bounds.min.y)).saturate()
        val unionZ = ((position.z - bounds.min.z) / (bounds.max.z - bounds.min.z)).saturate()

        val xIndex = floor(unionX * (dimensions.x - 1f))
        val yIndex = floor(unionY * (dimensions.y - 1f))
        val zIndex = floor(unionZ * (dimensions.z - 1f))

        return Vector3(xIndex, yIndex, zIndex)
    }

    private fun getKey(xIndex: Int, yIndex: Int, zIndex: Int): String {
        return "$xIndex.$yIndex.$zIndex"
    }
}
