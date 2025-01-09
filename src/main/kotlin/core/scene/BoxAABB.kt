package core.scene

import core.ecs.BaseComponent
import core.math.*
import core.math.Rect3dPlane.*

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
}