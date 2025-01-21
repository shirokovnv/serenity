package core.scene.camera

import core.math.Matrix4
import core.math.extensions.toRadians
import kotlin.math.tan

class PerspectiveCamera(
    width: Float,
    height: Float,
    fovY: Float,
    zNear: Float,
    zFar: Float
) : Camera() {

    var width: Float = width
        private set
    var height: Float = height
        private set
    var fovY: Float = fovY
        private set
    var zNear: Float = zNear
        private set
    var zFar: Float = zFar
        private set

    private var projectionM: Matrix4 = Matrix4()
    private var isProjParamsChanged: Boolean = true

    init {
        projectionType = ProjectionType.PERSPECTIVE
        setProjParams(width, height, fovY, zNear, zFar)
    }

    fun setProjParams(width: Float,
                      height: Float,
                      fovY: Float,
                      zNear: Float,
                      zFar: Float) {
        this.width = width
        this.height = height
        this.fovY = fovY
        this.zNear = zNear
        this.zFar = zFar
        isProjParamsChanged = true
    }

    override fun calculateProjectionMatrix(): Matrix4 {
        if (isProjParamsChanged) {
            val tanFOV = tan((fovY * 0.5f).toRadians())
            val aspectRatio = width / height

            projectionM[0, 0] = 1.0f / (tanFOV * aspectRatio)
            projectionM[0, 1] = 0.0f
            projectionM[0, 2] = 0.0f
            projectionM[0, 3] = 0.0f

            projectionM[1, 0] = 0.0f
            projectionM[1, 1] = 1.0f / tanFOV
            projectionM[1, 2] = 0.0f
            projectionM[1, 3] = 0.0f

            projectionM[2, 0] = 0.0f
            projectionM[2, 1] = 0.0f
            projectionM[2, 2] = (zFar + zNear) / (zNear - zFar)
            projectionM[2, 3] = (2.0f * zFar * zNear) / (zNear - zFar)

            projectionM[3, 0] = 0.0f
            projectionM[3, 1] = 0.0f
            projectionM[3, 2] = -1.0f
            projectionM[3, 3] = 0.0f

            isProjParamsChanged = false
        }

        return projectionM
    }
}