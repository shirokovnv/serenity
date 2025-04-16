package core.scene.camera

import core.ecs.BaseComponent
import core.math.Matrix4
import core.math.Vector3
import core.scene.Transform

abstract class Camera : BaseComponent() {
    protected lateinit var projectionType: ProjectionType
    private val yAxis = Vector3(0f, 1f, 0f)

    val transform: Transform
        get() = owner()?.getComponent<Transform>()!!

    val view: Matrix4
        get() = transform.matrix().invert()

    val projection: Matrix4
        get() = calculateProjectionMatrix()

    val viewProjection: Matrix4
        get() = projection * view

    fun projectionType(): ProjectionType = projectionType
    fun position(): Vector3 {
        val m = transform.matrix()
        return Vector3(m[0, 3], m[1, 3], m[2, 3])
    }
    fun forward(): Vector3 {
        val m = transform.matrix()
        return Vector3(-m[0, 2], -m[1, 2], -m[2, 2]).normalize()
    }
    fun up(): Vector3 {
        val m = transform.matrix()
        return Vector3(m[0, 1], m[1, 1], m[2, 1]).normalize()
    }
    fun right(): Vector3 {
        val m = transform.matrix()
        return Vector3(m[0, 0], m[1, 0], m[2, 0]).normalize()
    }

    protected abstract fun calculateProjectionMatrix(): Matrix4

    fun rotate(offset: Vector3) {
        transform.setRotation(transform.rotation() + offset)
    }

    fun rotateAroundHorizontalAxis(angle: Float) {
        val hAxis = up().cross(forward()).normalize()

        transform.rotateAroundAxis(angle, hAxis)
    }

    fun rotateAroundVerticalAxis(angle: Float) {
        transform.rotateAroundAxis(angle, yAxis)
    }

    fun move(offset: Vector3) {
        transform.setTranslation(transform.translation() + offset)
    }
}