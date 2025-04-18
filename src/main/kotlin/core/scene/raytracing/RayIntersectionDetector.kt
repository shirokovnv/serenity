package core.scene.raytracing

import core.math.Plane
import core.math.Rect3d
import core.math.Sphere
import core.math.Vector3
import kotlin.math.abs
import kotlin.math.sqrt

object RayIntersectionDetector {
    const val EPSILON = 1e-8

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
        var tmin = (rect3d.min.x - rayOrigin.x) / rayDirection.x
        var tmax = (rect3d.max.x - rayOrigin.x) / rayDirection.x

        if (tmin > tmax) {
            tmin = tmax.also { tmax = tmin }
        }

        var tymin = (rect3d.min.y - rayOrigin.y) / rayDirection.y
        var tymax = (rect3d.max.y - rayOrigin.y) / rayDirection.y

        if (tymin > tymax) {
            tymin = tymax.also { tymax = tymin }
        }

        if (tmin > tymax || tymin > tmax) {
            return null
        }

        if (tymin > tmin) tmin = tymin
        if (tymax < tmax) tmax = tymax

        var tzmin = (rect3d.min.z - rayOrigin.z) / rayDirection.z
        var tzmax = (rect3d.max.z - rayOrigin.z) / rayDirection.z

        if (tzmin > tzmax) {
            tzmin = tzmax.also { tzmax = tzmin }
        }

        if (tmin > tzmax || tzmin > tmax) {
            return null
        }

        if (tzmin > tmin) tmin = tzmin
        if (tzmax < tmax) tmax = tzmax

        return tmin
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

    fun rayIntersects(
        rayOrigin: Vector3,
        rayDirection: Vector3,
        triangleVertices: List<Vector3>
    ): Triple<Vector3, Vector3, Vector3>? {

        for (i in triangleVertices.indices step 3) {
            val rayTriangleIntersection = rayIntersects(
                rayOrigin,
                rayDirection,
                triangleVertices[i],
                triangleVertices[i + 1],
                triangleVertices[i + 2]
            )

            if (rayTriangleIntersection != null) {
                return Triple(
                    triangleVertices[i],
                    triangleVertices[i + 1],
                    triangleVertices[i + 2]
                )
            }
        }

        return null
    }

    // Moller-Trumbore intersection algrorithm
    fun rayIntersects(
        rayOrigin: Vector3,
        rayDirection: Vector3,
        v0: Vector3,
        v1: Vector3,
        v2: Vector3
    ): Float? {
        val edge1 = v1 - v0
        val edge2 = v2 - v0

        val planeVector = rayDirection.cross(edge2)
        val determinant = edge1.dot(planeVector)

        if (determinant < EPSILON && determinant > EPSILON) {
            return null
        }

        val inverseDeterminant = 1.0f / determinant
        val originToV0 = rayOrigin - v0
        val u = originToV0.dot(planeVector) * inverseDeterminant

        if (u < 0 || u > 1) {
            return null
        }

        val originToV0CrossEdge1 = originToV0.cross(edge1)
        val v = rayDirection.dot(originToV0CrossEdge1) * inverseDeterminant
        if (v < 0 || u + v > 1) {
            return null
        }

        return edge2.dot(originToV0CrossEdge1) * inverseDeterminant
    }
}