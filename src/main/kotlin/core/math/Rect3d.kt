package core.math

import kotlin.math.max
import kotlin.math.min

enum class Rect3dPlane {
    XY,
    XZ,
    YZ
}

data class Rect3d(val min: Vector3, val max: Vector3): Shape {

    constructor(other: Rect3d) : this(Vector3(other.min), Vector3(other.max))

    init {
        require(min.x <= max.x && min.y <= max.y && min.z <= max.z)
    }

    val width: Float
        get() = max.x - min.x

    val height: Float
        get() = max.y - min.y

    val depth: Float
        get() = max.z - min.z

    fun size() = Vector3(max.x - min.x, max.y - min.y, max.z - min.z)

    val center: Vector3
        get() = Vector3(
            (max.x + min.x) * 0.5f,
            (max.y + min.y) * 0.5f,
            (max.z + min.z) * 0.5f
            )

    val corners: List<Vector3>
        get() {
            return listOf(
                Vector3(min.x, min.y, min.z),
                Vector3(max.x, min.y, min.z),
                Vector3(min.x, max.y, min.z),
                Vector3(max.x, max.y, min.z),
                Vector3(min.x, min.y, max.z),
                Vector3(max.x, min.y, max.z),
                Vector3(min.x, max.y, max.z),
                Vector3(max.x, max.y, max.z)
            )
        }

    operator fun plusAssign(offset: Vector3) {
        min.x += offset.x
        max.x += offset.x
        min.y += offset.y
        max.y += offset.y
        min.z += offset.z
        max.z += offset.z
    }

    operator fun timesAssign(scale: Vector3) {
        min.x *= scale.x
        max.x *= scale.x
        min.y *= scale.y
        max.y *= scale.y
        min.z *= scale.z
        max.z *= scale.z
    }

    fun unionPoint(point: Vector3) {
        min.x = min(min.x, point.x)
        min.y = min(min.y, point.y)
        min.z = min(min.z, point.z)

        max.x = max(max.x, point.x)
        max.y = max(max.y, point.y)
        max.z = max(max.z, point.z)
    }
}