package core.scene.raytracing

import core.math.Plane
import core.math.Rect3d
import core.math.Sphere
import core.math.Vector3
import kotlin.math.abs
import kotlin.math.sqrt

object RayIntersectionDetector {
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
            val v0p0 = plane.normal * distance - rayOrigin
            val t = v0p0.dot(plane.normal) / denominator
            return if (t >= 0) t else null // Check outside
        }
        return null // Ray is collinear with plane
    }
}