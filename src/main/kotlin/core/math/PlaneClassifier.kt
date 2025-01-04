package core.math

object PlaneClassifier {

    fun classifyWithRect3d(plane: Plane, rect3d: Rect3d): Plane.PlaneClassification {
        val min = Vector3(rect3d.min)
        val max = Vector3(rect3d.max)

        if (plane.normal.x >= 0) {
            min.x = rect3d.max.x
            max.x = rect3d.min.x
        }

        if (plane.normal.y >= 0) {
            min.y = rect3d.max.y
            max.y = rect3d.min.y
        }

        if (plane.normal.z >= 0) {
            min.z = rect3d.max.z
            max.z = rect3d.min.z
        }

        val dMin = plane.signedDistance(min)
        val dMax = plane.signedDistance(max)

        return when {
            dMin < 0 -> Plane.PlaneClassification.PLANE_BACK
            dMax < 0 -> Plane.PlaneClassification.PLANE_INTERSECT
            else -> Plane.PlaneClassification.PLANE_FRONT
        }
    }

    fun classifyWithSphere(plane: Plane, sphere: Sphere): Plane.PlaneClassification {
        val signDist = plane.signedDistance(sphere.center)
        return when {
            signDist < -sphere.radius -> Plane.PlaneClassification.PLANE_BACK
            signDist < sphere.radius -> Plane.PlaneClassification.PLANE_INTERSECT
            else -> Plane.PlaneClassification.PLANE_FRONT
        }
    }
}