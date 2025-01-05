package core.scene.camera

import core.math.Matrix4
import core.math.Vector3

open class OrthographicCamera(
    override var position: Vector3,
    override var forward: Vector3,
    override var up: Vector3,
    val left: Float,
    val right: Float,
    val bottom: Float,
    val top: Float,
    val near: Float,
    val far: Float

) : Camera() {

    init {
        projectionType = ProjectionType.ORTHOGRAPHIC
        isChanged = true
    }

    override fun calculateProjectionMatrix(): Matrix4 {
        val m = Matrix4()

        m[0, 0] = 2.0f / (right - left)
        m[0, 1] = 0.0f
        m[0, 2] = 0.0f
        m[0, 3] = -(right + left) / (right - left)

        m[1, 0] = 0.0f
        m[1, 1] = 2.0f / (top - bottom)
        m[1, 2] = 0.0f
        m[1, 3] = -(top + bottom) / (top - bottom)

        m[2, 0] = 0.0f
        m[2, 1] = 0.0f
        m[2, 2] = 2.0f / (far - near)
        m[2, 3] = -(far + near) / (far - near)

        m[3, 0] = 0.0f
        m[3, 1] = 0.0f
        m[3, 2] = 0.0f
        m[3, 3] = 1.0f

        return m
    }
}