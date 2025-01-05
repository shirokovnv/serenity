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

    var isChanged: Boolean = false

    protected lateinit var view: Matrix4
    protected lateinit var projection: Matrix4
    protected lateinit var viewProjection: Matrix4

    fun projectionType(): ProjectionType = projectionType
    fun position(): Vector3 = Vector3(position)
    fun forward(): Vector3 = Vector3(forward)
    fun up(): Vector3 = Vector3(up)

    val rightOrientation: Vector3
        get() = up.cross(forward).normalize()

    val leftOrientation: Vector3
        get() = forward.cross(up).normalize()

    private var rotation: Quaternion = Quaternion();

    protected abstract fun calculateProjectionMatrix(): Matrix4

    fun rotate(axis: Vector3, angle: Float) {
        val q = Quaternion.fromAxisAngle(axis, angle)
        rotation = (q * rotation).normalize()
        isChanged = true
    }

    fun updatePosition(delta: Vector3) {
        position = position + delta
        isChanged = true
    }

    protected fun calculateViewMatrix(): Matrix4 {
        val newRight = rotation.rotate(up.cross(forward).normalize())
        val newUp = rotation.rotate(up)
        val newForward = rotation.rotate(forward)

        newForward.normalize()
        newUp.normalize()
        newRight.normalize()

//        val right = up.cross(forward).normalize()
        val view = Matrix4()
        val invPosition = -position

        view[0, 0] = newRight.x
        view[0, 1] = newRight.y
        view[0, 2] = newRight.z
        view[0, 3] = invPosition.x

        view[1, 0] = newUp.x
        view[1, 1] = newUp.y
        view[1, 2] = newUp.z
        view[1, 3] = invPosition.y

        view[2, 0] = newForward.x
        view[2, 1] = newForward.y
        view[2, 2] = newForward.z
        view[2, 3] = invPosition.z

        view[3, 0] = 0.0f
        view[3, 1] = 0.0f
        view[3, 2] = 0.0f
        view[3, 3] = 1.0f

        return view
    }

    fun view(): Matrix4 {
        if (isChanged) {
            view = calculateViewMatrix()
        }

        return calculateViewMatrix()
    }

    fun projection(): Matrix4 {
        if (isChanged) {
            projection = calculateProjectionMatrix()
        }

        return calculateProjectionMatrix()
    }

    fun viewProjection(): Matrix4 {
        if (isChanged) {
            viewProjection = calculateViewMatrix() * calculateProjectionMatrix()
        }

        return viewProjection
    }
}