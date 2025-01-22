package core.scene.spatial

import core.math.Rect3d
import core.math.Vector3
import core.math.extensions.clamp
import core.math.extensions.toIntFloor
import kotlin.math.max

class LinearQuadTreeRect(
    var x0: Int = 0,
    var x1: Int = 0,
    var y0: Int = 0,
    var y1: Int = 0,
    var z0: Int = 0,
    var z1: Int = 0
) {
    constructor(other: LinearQuadTreeRect) : this(other.x0, other.x1, other.y0, other.y1, other.z0, other.z1)

    fun convert(worldRect: Rect3d, offset: Vector3, scale: Vector3): LinearQuadTreeRect {
        val convertedRect = Rect3d(worldRect)

        convertedRect += offset
        convertedRect *= scale

        convertedRect.max.x -= 0.01f
        convertedRect.max.y -= 0.01f
        convertedRect.max.z -= 0.01f


        convertedRect.max.x = max(convertedRect.max.x, convertedRect.min.x)
        convertedRect.max.y = max(convertedRect.max.y, convertedRect.min.y)
        convertedRect.max.z = max(convertedRect.max.z, convertedRect.min.z)

        x0 = convertedRect.min.x.toIntFloor()
        x1 = convertedRect.max.x.toIntFloor()
        y0 = convertedRect.min.y.toIntFloor()
        y1 = convertedRect.max.y.toIntFloor()
        z0 = convertedRect.min.z.toIntFloor()
        z1 = convertedRect.max.z.toIntFloor()

        x0 = x0.clamp(0, 254)
        y0 = y0.clamp(0, 30)
        z0 = z0.clamp(0, 254)

        x1 = x1.clamp(x0 + 1, 255)
        y1 = y1.clamp(y0 + 1, 31)
        z1 = z1.clamp(z0 + 1, 255)

        return this
    }
}