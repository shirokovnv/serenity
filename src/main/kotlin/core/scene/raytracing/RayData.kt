package core.scene.raytracing

import core.math.Vector3

data class RayData(val origin: Vector3, val direction: Vector3, val length: Float, val timestamp: Long)