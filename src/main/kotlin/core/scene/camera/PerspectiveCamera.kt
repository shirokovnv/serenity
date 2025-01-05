package core.scene.camera

import core.math.Matrix4
import core.math.Vector3
import core.math.toRadians
import kotlin.math.tan

class PerspectiveCamera(
    override var position: Vector3,
    override var forward: Vector3,
    override var up: Vector3,
    val width: Float,
    val height: Float,
    val fovY: Float,
    val zNear: Float,
    val zFar: Float
) : Camera() {

    init {
        projectionType = ProjectionType.PERSPECTIVE

        up.normalize()
        forward.normalize()
    }

    override fun calculateProjectionMatrix(): Matrix4 {
        val tanFOV = tan((fovY * 0.5f).toRadians())
        val aspectRatio = width / height
        val m = Matrix4()

        m[0, 0] = 1.0f / (tanFOV * aspectRatio)
        m[0, 1] = 0.0f
        m[0, 2] = 0.0f
        m[0, 3] = 0.0f

        m[1, 0] = 0.0f
        m[1, 1] = 1.0f / tanFOV
        m[1, 2] = 0.0f
        m[1, 3] = 0.0f

        m[2, 0] = 0.0f
        m[2, 1] = 0.0f
        m[2, 2] = zFar / (zFar - zNear)
        m[2, 3] = zFar * zNear / (zFar - zNear)

        m[3, 0] = 0.0f
        m[3, 1] = 0.0f
        m[3, 2] = 1.0f
        m[3, 3] = 1.0f

        return m
    }
}