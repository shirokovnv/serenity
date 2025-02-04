package core.scene.volumes

import core.ecs.BaseComponent
import core.math.*
import core.math.Rect3dPlane.*
import core.scene.Transform
import kotlin.math.max
import kotlin.math.min

class BoxAABB(private var shape: Rect3d) : BaseComponent(), Volumetric<Rect3d> {
    override fun setShape(newShape: Rect3d) {
        shape = newShape
    }

    override fun shape(): Rect3d {
        return shape
    }

    fun toRect2d(rect3dPlane: Rect3dPlane = XY): Rect2d {
        return when (rect3dPlane) {
            XY -> Rect2d(Vector2(shape.min.x, shape.min.y), Vector2(shape.max.x, shape.max.y))
            XZ -> Rect2d(Vector2(shape.min.x, shape.min.z), Vector2(shape.max.x, shape.max.z))
            YZ -> Rect2d(Vector2(shape.min.y, shape.min.z), Vector2(shape.max.y, shape.max.z))
        }
    }

    fun transform(transform: Transform) {
        val corners = shape.corners

        val transformedCorners = corners.map { (transform.matrix() * Quaternion(it, 1f)).xyz() }
        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var minZ = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var maxY = Float.MIN_VALUE
        var maxZ = Float.MIN_VALUE

        transformedCorners.forEach {
            minX = min(minX, it.x)
            minY = min(minY, it.y)
            minZ = min(minZ, it.z)
            maxX = max(maxX, it.x)
            maxY = max(maxY, it.y)
            maxZ = max(maxZ, it.z)
        }

        setShape(Rect3d(Vector3(minX, minY, minZ), Vector3(maxX, maxY, maxZ)))
    }
}