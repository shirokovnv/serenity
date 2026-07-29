package modules.terrain.quadtree

import core.math.Quaternion
import core.math.Vector2
import core.math.Vector3

data class QuadTreeInstanceData(
    val topLeft: Vector2,
    val scale: Vector3,
    val lod: Quaternion,
    val lowPoint: Vector3,
    val highPoint: Vector3
)