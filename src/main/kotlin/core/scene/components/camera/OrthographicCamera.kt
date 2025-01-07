package core.scene.components.camera

import core.math.Matrix4

class OrthographicCamera(
    val leftBound: Float,
    val rightBound: Float,
    val bottomBound: Float,
    val topBound: Float,
    val nearBound: Float,
    val farBound: Float

) : Camera() {

    init {
        projectionType = ProjectionType.ORTHOGRAPHIC
    }

    override fun calculateProjectionMatrix(): Matrix4 {
        val m = Matrix4()

        m[0, 0] = 2.0f / (rightBound - leftBound)
        m[0, 1] = 0.0f
        m[0, 2] = 0.0f
        m[0, 3] = -(rightBound + leftBound) / (rightBound - leftBound)

        m[1, 0] = 0.0f
        m[1, 1] = 2.0f / (topBound - bottomBound)
        m[1, 2] = 0.0f
        m[1, 3] = -(topBound + bottomBound) / (topBound - bottomBound)

        m[2, 0] = 0.0f
        m[2, 1] = 0.0f
        m[2, 2] = 2.0f / (farBound - nearBound)
        m[2, 3] = -(farBound + nearBound) / (farBound - nearBound)

        m[3, 0] = 0.0f
        m[3, 1] = 0.0f
        m[3, 2] = 0.0f
        m[3, 3] = 1.0f

        return m
    }
}