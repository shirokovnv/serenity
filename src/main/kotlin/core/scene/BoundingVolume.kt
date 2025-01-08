package core.scene

import core.ecs.BaseComponent
import core.math.*

class BoundingVolume(private var shape: Shape) : BaseComponent(), Volumetric {

    fun intersectsWith(volumetric: Volumetric): Boolean {

        if (shape is Rect2d && volumetric.shape() is Rect2d) {
            return ShapeIntersector.intersects(shape as Rect2d, volumetric.shape() as Rect2d)
        }

        if (shape is Rect3d && volumetric.shape() is Rect3d) {
            return ShapeIntersector.intersects(shape as Rect3d, volumetric.shape() as Rect3d)
        }

        if (shape is Sphere && volumetric.shape() is Sphere) {
            return ShapeIntersector.intersects(shape as Sphere, volumetric.shape() as Sphere)
        }

        if (shape is Rect3d && volumetric.shape() is Sphere) {
            return ShapeIntersector.intersects(shape as Rect3d, volumetric.shape() as Sphere)
        }

        if (shape is Sphere && volumetric.shape() is Rect3d) {
            return ShapeIntersector.intersects(volumetric.shape() as Rect3d, shape as Sphere)
        }

        throw IllegalArgumentException("Intersection not supported.")
    }

    override fun setShape(newShape: Shape) {
        shape = newShape
    }

    override fun shape(): Shape {
        return shape
    }
}