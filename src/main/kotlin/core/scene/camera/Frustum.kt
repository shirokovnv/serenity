package core.scene.camera

import core.math.*
import core.scene.BoundingVolume
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.math.tan

class Frustum(private val camera: PerspectiveCamera, private val normalizePlanes: Boolean = true) {
    private var topPlane = Plane.fromPoint(Vector3(0f), Vector3(0f))
    private var bottomPlane = Plane.fromPoint(Vector3(0f), Vector3(0f))
    private var leftPlane = Plane.fromPoint(Vector3(0f), Vector3(0f))
    private var rightPlane = Plane.fromPoint(Vector3(0f), Vector3(0f))
    private var nearPlane = Plane.fromPoint(Vector3(0f), Vector3(0f))
    private var farPlane = Plane.fromPoint(Vector3(0f), Vector3(0f))

    private var searchVolume = BoundingVolume(
        Rect3d(Vector3(0f), Vector3(0f))
    )

    data class FrustumPlaneSize(
        val nearWidth: Float,
        val nearHeight: Float,
        val farWidth: Float,
        val farHeight: Float
    )

    init {
        recalculatePlanes()
        recalculateSearchVolume()
    }

    fun searchVolume(): BoundingVolume {
        return searchVolume
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
        val cameraPosition = camera.position()
        val cameraRight = camera.right()
        val cameraForward = camera.forward()
        val cameraUp = camera.up()

        // Build a box around frustum
        val fpSize = calculateFrustumSize(
            camera.zNear,
            camera.zFar,
            camera.fovY,
            camera.width,
            camera.height
        )

        val corners = calculateFrustumCorners(
            cameraPosition,
            fpSize,
            camera.zNear,
            camera.zFar
        )

        val orientedCorners = applyCameraOrientation(corners, cameraPosition, cameraForward, cameraUp, cameraRight)

        val searchRect = calculateBoundingBox(orientedCorners)
        searchVolume.setShape(searchRect)
    }

    private fun calculateFrustumSize(
        zNear: Float,
        zFar: Float,
        fovY: Float,
        screenWidth: Float,
        screenHeight: Float
    ): FrustumPlaneSize {
        val aspectRatio = screenWidth / screenHeight

        // Calculate near plane size
        val nearHeight = 2 * tan(fovY / 2 * (PI.toFloat() / 180f)) * zNear
        val nearWidth = nearHeight * aspectRatio

        // Calculate far plane size
        val farHeight = 2 * tan(fovY / 2 * (PI.toFloat() / 180f)) * zFar
        val farWidth = farHeight * aspectRatio

        return FrustumPlaneSize(
            nearWidth,
            nearHeight,
            farWidth,
            farHeight
        )
    }

    private fun calculateFrustumCorners(
        cameraPosition: Vector3,
        fpSize: FrustumPlaneSize,
        zNear: Float, zFar: Float
    ): List<Vector3> {
        val nearCorners = listOf(
            Vector3(
                cameraPosition.x - fpSize.nearWidth / 2,
                cameraPosition.y - fpSize.nearHeight / 2,
                cameraPosition.z + zNear),
            Vector3(
                cameraPosition.x + fpSize.nearWidth / 2,
                cameraPosition.y - fpSize.nearHeight / 2,
                cameraPosition.z + zNear),
            Vector3(
                cameraPosition.x + fpSize.nearWidth / 2,
                cameraPosition.y + fpSize.nearHeight / 2,
                cameraPosition.z + zNear),
            Vector3(
                cameraPosition.x - fpSize.nearWidth / 2,
                cameraPosition.y + fpSize.nearHeight / 2,
                cameraPosition.z + zNear)
        )

        val farCorners = listOf(
            Vector3(
                cameraPosition.x - fpSize.farWidth / 2,
                cameraPosition.y - fpSize.farHeight / 2,
                cameraPosition.z + zFar),
            Vector3(
                cameraPosition.x + fpSize.farWidth / 2,
                cameraPosition.y - fpSize.farHeight / 2,
                cameraPosition.z + zFar),
            Vector3(
                cameraPosition.x + fpSize.farWidth / 2,
                cameraPosition.y + fpSize.farHeight / 2,
                cameraPosition.z + zFar),
            Vector3(
                cameraPosition.x - fpSize.farWidth / 2,
                cameraPosition.y + fpSize.farHeight / 2,
                cameraPosition.z + zFar)
        )

        return nearCorners + farCorners
    }

    private fun applyCameraOrientation(
        corners: List<Vector3>,
        cameraPosition: Vector3,
        cameraForward: Vector3,
        cameraUp: Vector3,
        cameraRight: Vector3
    ): List<Vector3> {
        return corners.map { corner ->
            Vector3(
                cameraPosition.x + corner.x * cameraRight.x + corner.y * cameraUp.x + corner.z * cameraForward.x,
                cameraPosition.y + corner.x * cameraRight.y + corner.y * cameraUp.y + corner.z * cameraForward.y,
                cameraPosition.z + corner.x * cameraRight.z + corner.y * cameraUp.z + corner.z * cameraForward.z
            )
        }
    }

    private fun calculateBoundingBox(corners: List<Vector3>): Rect3d {
        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var minZ = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var maxY = Float.MIN_VALUE
        var maxZ = Float.MIN_VALUE

        for (corner in corners) {
            minX = min(minX, corner.x)
            minY = min(minY, corner.y)
            minZ = min(minZ, corner.z)
            maxX = max(maxX, corner.x)
            maxY = max(maxY, corner.y)
            maxZ = max(maxZ, corner.z)
        }

        return Rect3d(Vector3(minX, minY, minZ), Vector3(maxX, maxY, maxZ))
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