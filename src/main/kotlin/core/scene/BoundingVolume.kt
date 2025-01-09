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

    fun width(): Float {
        return when (shape) {
            is Rect3d -> (shape as Rect3d).width
            is Rect2d -> (shape as Rect2d).width
            is Sphere -> (shape as Sphere).radius
            else -> throw IllegalStateException("Unsupported shape type.")
        }
    }

    fun height(): Float {
        return when (shape) {
            is Rect3d -> (shape as Rect3d).height
            is Rect2d -> (shape as Rect2d).height
            is Sphere -> (shape as Sphere).radius
            else -> throw IllegalStateException("Unsupported shape type.")
        }
    }

    fun depth(): Float {
        return when (shape) {
            is Rect3d -> (shape as Rect3d).depth
            is Rect2d -> 0.0f
            is Sphere -> (shape as Sphere).radius
            else -> throw IllegalStateException("Unsupported shape type.")
        }
    }

    fun toRect2d(): Rect2d {
        return when (shape) {
            is Rect2d -> shape as Rect2d

            is Rect3d -> {
                val rect = shape as Rect3d
                Rect2d(Vector2(rect.min.x, rect.min.z), Vector2(rect.max.x, rect.max.z))
            }

            is Sphere -> {
                val sphere = shape as Sphere
                Rect2d(
                    Vector2(sphere.center.x - sphere.radius, sphere.center.z - sphere.radius),
                    Vector2(sphere.center.x + sphere.radius, sphere.center.z + sphere.radius)
                )
            }

            else -> throw IllegalStateException("Unsupported shape type.")
        }
    }
}