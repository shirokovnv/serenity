package core.scene.raytracing

import core.math.*
import kotlin.math.abs
import kotlin.math.sqrt

typealias RayIntersectFunction = (Vector3) -> Boolean

object RayIntersectionDetector {
    private const val MAX_BINARY_SEARCH_DEPTH = 200

    fun rayIntersects(rayOrigin: Vector3, rayDirection: Vector3, sphere: Sphere): Float? {
        val oc = rayOrigin - sphere.center
        val a = rayDirection.dot(rayDirection)
        val b = 2f * oc.dot(rayDirection)
        val c = oc.dot(oc) - sphere.radius * sphere.radius
        val discriminant = b * b - 4f * a * c

        if (discriminant < 0f) return null

        val t1 = (-b - sqrt(discriminant)) / (2f * a)
        val t2 = (-b + sqrt(discriminant)) / (2f * a)

        return if (t1 > 0f && t2 > 0f) minOf(t1, t2)
        else if (t1 > 0f) t1
        else if (t2 > 0f) t2
        else null
    }

    fun rayIntersects(
        rayOrigin: Vector3,
        rayDirection: Vector3,
        point: Vector3,
        threshold: Float = 0.01f
    ): Float? {
        val op = point - rayOrigin
        val t = op.dot(rayDirection)
        if (t < 0) return null

        val closestPoint = rayOrigin + rayDirection * t
        if ((closestPoint - point).length() < threshold) {
            return t
        }
        return null
    }

    fun rayIntersects(rayOrigin: Vector3, rayDirection: Vector3, rect3d: Rect3d): Float? {
        var tmin = Float.NEGATIVE_INFINITY
        var tmax = Float.POSITIVE_INFINITY

        val bounds = arrayOf(rect3d.min, rect3d.max)

        for (i in 0..2) {
            val t1 = (bounds[0][i] - rayOrigin[i]) / rayDirection[i]
            val t2 = (bounds[1][i] - rayOrigin[i]) / rayDirection[i]

            tmin = maxOf(tmin, minOf(t1, t2))
            tmax = minOf(tmax, maxOf(t1, t2))
        }

        if (tmax >= tmin && tmin > 0) {
            return tmin
        }

        return null
    }

    fun rayIntersects(rayOrigin: Vector3, rayDirection: Vector3, plane: Plane): Float? {
        val denominator = rayDirection.dot(plane.normal)
        if (abs(denominator) > 1e-6f) { // Check ray-plane collinear
            val distance = plane.distance
            val point = plane.normal * distance - rayOrigin
            val t = point.dot(plane.normal) / denominator
            return if (t >= 0) t else null // Check outside
        }
        return null // Ray is collinear with plane
    }

    fun rayIntersectsInRange(
        rayOrigin: Vector3,
        rayDirection: Vector3,
        range: Vector2,
        rect3d: Rect3d
    ): Boolean {
        val fn: RayIntersectFunction = { point -> IntersectionDetector.intersects(rect3d, point) }
        return rayIntersectsInRange(rayOrigin, rayDirection, range, fn)
    }

    fun rayIntersectsInRange(
        rayOrigin: Vector3,
        rayDirection: Vector3,
        range: Vector2,
        sphere: Sphere
    ): Boolean {
        val fn: RayIntersectFunction = { point -> IntersectionDetector.intersects(sphere, point) }
        return rayIntersectsInRange(rayOrigin, rayDirection, range, fn)
    }

    fun rayIntersectsInRange(
        rayOrigin: Vector3,
        rayDirection: Vector3,
        range: Vector2,
        rayIntersectFn: RayIntersectFunction
    ): Boolean {
        val startPoint = getPointOnRay(rayOrigin, rayDirection, range.x)
        val endPoint = getPointOnRay(rayOrigin, rayDirection, range.y)

        return !rayIntersectFn(startPoint) &&
                rayIntersectFn(endPoint)
    }

    fun rayBinarySearchIntersects(
        rayOrigin: Vector3,
        rayDirection: Vector3,
        range: Vector2,
        rect3d: Rect3d,
        depth: Int = MAX_BINARY_SEARCH_DEPTH
    ): Vector3? {
        val fn: RayIntersectFunction = { point -> IntersectionDetector.intersects(rect3d, point) }

        return rayBinarySearchIntersects(
            rayOrigin,
            rayDirection,
            range,
            fn,
            depth
        )
    }

    fun rayBinarySearchIntersects(
        rayOrigin: Vector3,
        rayDirection: Vector3,
        range: Vector2,
        sphere: Sphere,
        depth: Int = 0
    ): Vector3? {
        val fn: RayIntersectFunction = { point -> IntersectionDetector.intersects(sphere, point) }

        return rayBinarySearchIntersects(
            rayOrigin,
            rayDirection,
            range,
            fn,
            depth
        )
    }

    fun rayBinarySearchIntersects(
        rayOrigin: Vector3,
        rayDirection: Vector3,
        range: Vector2,
        rayIntersectFn: RayIntersectFunction,
        depth: Int = 0
    ): Vector3? {
        val half = range.x + ((range.y - range.x) / 2f)

        if (depth >= MAX_BINARY_SEARCH_DEPTH) {
            val endPoint = getPointOnRay(rayOrigin, rayDirection, half)
            return if (rayIntersectFn(endPoint)) endPoint else null
        }

        if (rayIntersectsInRange(rayOrigin, rayDirection, Vector2(range.x, half), rayIntersectFn)) {
            return rayBinarySearchIntersects(rayOrigin, rayDirection, Vector2(range.x, half), rayIntersectFn, depth + 1)
        }

        return rayBinarySearchIntersects(rayOrigin, rayDirection, Vector2(half, range.y), rayIntersectFn, depth + 1)
    }
}