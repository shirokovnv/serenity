package core.scene.spatial

import core.scene.BoundingVolume
import core.scene.Object

interface SpatialPartitioningInterface {
    fun insert(obj: Object)
    fun remove(obj: Object): Boolean
    fun countObjects(): Int
    fun buildSearchResults(searchVolume: BoundingVolume): List<Object>
}