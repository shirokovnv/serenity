package modules.terrain.cdlod

import core.math.Vector2
import core.math.Vector3

data class CdlodInstanceData(
    val topLeft: Vector2,
    val scale: Vector3,
    val lod: Float,
    val lowPoint: Vector3,
    val highPoint: Vector3
)