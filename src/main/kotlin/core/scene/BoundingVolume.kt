package core.scene

import core.ecs.BaseComponent
import core.math.*

class BoundingVolume(private var shape: Shape) : BaseComponent(), Volumetric {

    fun intersects(volumetric: Volumetric): Boolean {
        return when {
            shape is Rect2d && volumetric.shape() is Rect2d ->
                IntersectionDetector.intersects(shape as Rect2d, volumetric.shape() as Rect2d)

            shape is Rect3d && volumetric.shape() is Rect3d ->
                IntersectionDetector.intersects(shape as Rect3d, volumetric.shape() as Rect3d)

            shape is Sphere && volumetric.shape() is Sphere ->
                IntersectionDetector.intersects(shape as Sphere, volumetric.shape() as Sphere)

            shape is Rect3d && volumetric.shape() is Sphere ->
                IntersectionDetector.intersects(shape as Rect3d, volumetric.shape() as Sphere)

            shape is Sphere && volumetric.shape() is Rect3d ->
                return IntersectionDetector.intersects(volumetric.shape() as Rect3d, shape as Sphere)

            else -> throw IllegalArgumentException("Unsupported shape type.")
        }
    }

    fun contains(volumetric: Volumetric): Boolean {
        return when {
            shape is Rect2d && volumetric.shape() is Rect2d ->
                OverlapDetector.contains(shape as Rect2d, volumetric.shape() as Rect2d)

            shape is Rect3d && volumetric.shape() is Rect3d ->
                OverlapDetector.contains(shape as Rect3d, volumetric.shape() as Rect3d)

            shape is Sphere && volumetric.shape() is Sphere ->
                OverlapDetector.contains(shape as Sphere, volumetric.shape() as Sphere)

            shape is Sphere && volumetric.shape() is Rect3d ->
                OverlapDetector.contains(shape as Sphere, volumetric.shape() as Rect3d)

            shape is Rect3d && volumetric.shape() is Sphere ->
                OverlapDetector.contains(shape as Rect3d, volumetric.shape() as Sphere)

            else -> throw IllegalArgumentException("Unsupported shape type.")
        }
    }

    override fun setShape(newShape: Shape) {
        shape = newShape
    }

    override fun shape(): Shape {
        return shape
    }
}