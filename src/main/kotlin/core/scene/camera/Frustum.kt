package core.scene.camera

import core.math.*
import core.math.extensions.toRadians
import core.scene.volumes.BoxAABB
import kotlin.math.*

class Frustum(private val camera: PerspectiveCamera, private val normalizePlanes: Boolean = true) {
    private var topPlane = Plane.fromPoint(Vector3(0f), Vector3(0f))
    private var bottomPlane = Plane.fromPoint(Vector3(0f), Vector3(0f))
    private var leftPlane = Plane.fromPoint(Vector3(0f), Vector3(0f))
    private var rightPlane = Plane.fromPoint(Vector3(0f), Vector3(0f))
    private var nearPlane = Plane.fromPoint(Vector3(0f), Vector3(0f))
    private var farPlane = Plane.fromPoint(Vector3(0f), Vector3(0f))

    private var searchVolume = BoxAABB(
        Rect3d(Vector3(0f), Vector3(0f))
    )

    init {
        recalculatePlanes()
        recalculateSearchVolume()
    }

    fun searchVolume(): BoxAABB {
        return searchVolume
    }

    // @see https://lxjk.github.io/2017/04/15/Calculate-Minimal-Bounding-Sphere-of-Frustum.html
    fun calculateBoundingSphere(): Sphere {
        val w = camera.width
        val h = camera.height
        val near = camera.zNear
        val far = camera.zFar
        val fov = camera.fovY

        val k = sqrt((1 + (h * h) / (w * w))) * tan((fov * 0.5f).toRadians())
        val k2 = k * k
        val k4 = k2 * k2

        val center: Vector3
        val radius: Float

        if (k2 >= (far - near) / (far + near)) {
            center = Vector3(0f, 0f, -far)
            radius = far * k
        } else {
            center = Vector3(0f, 0f, -0.5f * (far + near) * (1 + k2))
            radius = 0.5f * sqrt(((far - near) * (far - near) + 2 * (far * far + near * near) * k2 + (far + near) * (far + near) * k4))
        }

        return Sphere(center, radius)
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

    fun recalculateSearchVolume() {
        val frustumSphere = calculateBoundingSphere()
        val worldSphereCenter = (camera.view.invert() * Quaternion(frustumSphere.center, 1f)).xyz()
        val searchRectMin = Vector3(
            worldSphereCenter.x - frustumSphere.radius,
            worldSphereCenter.y - frustumSphere.radius,
            worldSphereCenter.z - frustumSphere.radius,
        )
        val searchRectMax = Vector3(
            worldSphereCenter.x + frustumSphere.radius,
            worldSphereCenter.y + frustumSphere.radius,
            worldSphereCenter.z + frustumSphere.radius,
        )

        searchVolume.setShape(Rect3d(searchRectMin, searchRectMax))
    }

    fun checkSphereInFrustum(sphere: Sphere): Boolean {
        return !(PlaneClassifier.classifyWithSphere(leftPlane, sphere) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithSphere(rightPlane, sphere) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithSphere(topPlane, sphere) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithSphere(bottomPlane, sphere) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithSphere(nearPlane, sphere) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithSphere(farPlane, sphere) == Plane.PlaneClassification.PLANE_BACK
                )
    }

    fun checkRect3dInFrustum(rect3d: Rect3d): Boolean {
        return !(PlaneClassifier.classifyWithRect3d(leftPlane, rect3d) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithRect3d(rightPlane, rect3d) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithRect3d(topPlane, rect3d) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithRect3d(bottomPlane, rect3d) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithRect3d(nearPlane, rect3d) == Plane.PlaneClassification.PLANE_BACK
                || PlaneClassifier.classifyWithRect3d(farPlane, rect3d) == Plane.PlaneClassification.PLANE_BACK
                )
    }
}