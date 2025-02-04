package core.scene.spatial

import core.scene.volumes.BoxAABB
import core.scene.Object

interface SpatialPartitioningInterface {
    fun insert(obj: Object): Boolean
    fun remove(obj: Object): Boolean
    fun countObjects(): Int
    fun buildSearchResults(searchVolume: BoxAABB): List<Object>
}