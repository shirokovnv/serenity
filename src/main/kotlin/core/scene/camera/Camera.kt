package core.scene.camera

import core.ecs.Component
import core.math.Matrix4
import core.math.Quaternion
import core.math.Vector3

abstract class Camera : Component {
    protected lateinit var projectionType: ProjectionType
    protected abstract var position: Vector3
    protected abstract var forward: Vector3
    protected abstract var up: Vector3

    val view: Matrix4
        get() = calculateViewMatrix()

    val projection: Matrix4
        get() = calculateProjectionMatrix()

    val viewProjection: Matrix4
        get() = projection * view

    fun projectionType(): ProjectionType = projectionType
    fun position(): Vector3 = Vector3(position)
    fun forward(): Vector3 = Vector3(forward)
    fun up(): Vector3 = Vector3(up)

    private var rotation: Quaternion = Quaternion();

    protected abstract fun calculateProjectionMatrix(): Matrix4

    fun updateRotation(axis: Vector3, angle: Float) {
        val q = Quaternion.fromAxisAngle(axis, angle)
        rotation = (q * rotation).normalize()
    }

    fun updatePosition(delta: Vector3) {
        position = position + delta
    }

    protected fun calculateViewMatrix(): Matrix4 {
        val newRight = rotation.rotate(up.cross(forward).normalize())
        val newUp = rotation.rotate(up)
        val newForward = rotation.rotate(forward)

        newForward.normalize()
        newUp.normalize()
        newRight.normalize()

        val m = Matrix4()

        m[0, 0] = newRight.x
        m[0, 1] = newRight.y
        m[0, 2] = newRight.z
        m[0, 3] = -position.x

        m[1, 0] = newUp.x
        m[1, 1] = newUp.y
        m[1, 2] = newUp.z
        m[1, 3] = -position.y

        m[2, 0] = newForward.x
        m[2, 1] = newForward.y
        m[2, 2] = newForward.z
        m[2, 3] = -position.z

        m[3, 0] = 0.0f
        m[3, 1] = 0.0f
        m[3, 2] = 0.0f
        m[3, 3] = 1.0f

        return m
    }
}