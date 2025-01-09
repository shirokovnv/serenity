package core.scene.spatial

import core.scene.BoundingVolume
import core.scene.Object

interface SpatialPartitioningInterface {
    fun addOrUpdateSceneObject(obj: Object)
    fun remove(obj: Object)
    fun countObjects(): Int
    fun findCollisions(volume: BoundingVolume): List<Object>
}