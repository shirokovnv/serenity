package core.math

import kotlin.math.abs

class Plane(var normal: Vector3, var distance: Float) {

    enum class PlaneClassification {
        PLANE_FRONT,
        PLANE_BACK,
        PLANE_INTERSECT
    }

    companion object {
        fun fromPoint(point: Vector3, normal: Vector3): Plane {
            val n = normal.normalize()
            return Plane(n, n.dot(point))
        }
    }

    fun normalize(): Plane {
        val invMag = 1.0f / normal.length()
        normal = normal * invMag
        distance *= invMag
        return this
    }

    fun signedDistance(point: Vector3): Float {
        return normal.dot(point) + distance
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Plane) return false
        return  this.normal == other.normal && abs(this.distance - other.distance) < 0.00001f
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + normal.hashCode()
        result = 31 * result + distance.hashCode()
        return result
    }
}