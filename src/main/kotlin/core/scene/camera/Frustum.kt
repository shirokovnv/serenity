package core.scene.camera

import core.math.*
import core.scene.BoundingVolume

class Frustum(private val camera: Camera, private val normalizePlanes: Boolean = true) {
    private var topPlane = Plane.fromPoint(Vector3(0f), Vector3(0f))
    private var bottomPlane = Plane.fromPoint(Vector3(0f), Vector3(0f))
    private var leftPlane = Plane.fromPoint(Vector3(0f), Vector3(0f))
    private var rightPlane = Plane.fromPoint(Vector3(0f), Vector3(0f))
    private var nearPlane = Plane.fromPoint(Vector3(0f), Vector3(0f))
    private var farPlane = Plane.fromPoint(Vector3(0f), Vector3(0f))

    init {
        recalculatePlanes()
    }

    fun recalculatePlanes() {
        val m = camera.viewProjection

        leftPlane.normal.x = m[3, 0] + m[0, 0]
        leftPlane.normal.y = m[3, 1] + m[0, 1]
        leftPlane.normal.z = m[3, 2] + m[0, 2]
        leftPlane.distance = m[3, 3] + m[0, 3]

        rightPlane.normal.x = m[3, 0] - m[0, 0]
        rightPlane.normal.y = m[3, 1] - m[0, 1]
        rightPlane.normal.z = m[3, 2] - m[0, 2]
        rightPlane.distance = m[3, 3] - m[0, 3]

        topPlane.normal.x = m[3, 0] - m[1, 0]
        topPlane.normal.y = m[3, 1] - m[1, 1]
        topPlane.normal.z = m[3, 2] - m[1, 2]
        topPlane.distance = m[3, 3] - m[1, 3]

        bottomPlane.normal.x = m[3, 0] + m[1, 0]
        bottomPlane.normal.y = m[3, 1] + m[1, 1]
        bottomPlane.normal.z = m[3, 2] + m[1, 2]
        bottomPlane.distance = m[3, 3] + m[1, 3]

        nearPlane.normal.x = m[3, 0] + m[2, 0]
        nearPlane.normal.y = m[3, 1] + m[2, 1]
        nearPlane.normal.z = m[3, 2] + m[2, 2]
        nearPlane.distance = m[3, 3] + m[2, 3]

        farPlane.normal.x = m[3, 0] - m[2, 0]
        farPlane.normal.y = m[3, 1] - m[2, 1]
        farPlane.normal.z = m[3, 2] - m[2, 2]
        farPlane.distance = m[3, 3] - m[2, 3]

        if (normalizePlanes) {
            leftPlane.normalize()
            rightPlane.normalize()
            topPlane.normalize()
            bottomPlane.normalize()
            nearPlane.normalize()
            farPlane.normalize()
        }
    }

    fun checkIsOnFrustum(boundingVolume: BoundingVolume): Boolean {
        return when(boundingVolume.shape()) {
            is Rect3d -> checkRect3dInFrustum(boundingVolume.shape() as Rect3d)
            is Sphere -> checkSphereInFrustum(boundingVolume.shape() as Sphere)
            else -> throw IllegalStateException("Unsupported bounding volume shape.")
        }
    }

    private fun checkSphereInFrustum(sphere: Sphere): Boolean {
        return !(PlaneClassifier.classifyWithSphere(leftPlane, sphere) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithSphere(rightPlane, sphere) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithSphere(topPlane, sphere) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithSphere(bottomPlane, sphere) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithSphere(nearPlane, sphere) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithSphere(farPlane, sphere) == Plane.PlaneClassification.PLANE_BACK
                )
    }

    private fun checkRect3dInFrustum(rect3d: Rect3d): Boolean {
        return !(PlaneClassifier.classifyWithRect3d(leftPlane, rect3d) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithRect3d(rightPlane, rect3d) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithRect3d(topPlane, rect3d) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithRect3d(bottomPlane, rect3d) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithRect3d(nearPlane, rect3d) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithRect3d(farPlane, rect3d) == Plane.PlaneClassification.PLANE_BACK
                )
    }
}