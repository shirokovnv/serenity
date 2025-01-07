package core.scene.components

import core.ecs.Component
import core.math.Matrix4
import core.math.Vector3
import kotlin.math.cos
import kotlin.math.sin

class Transform : Component() {
    private var translation: Vector3 = Vector3(0f, 0f, 0f)
    private var rotation: Vector3 = Vector3(0f, 0f, 0f)
    private var scale: Vector3 = Vector3(1f, 1f, 1f)
    private var matrix: Matrix4 = Matrix4().identity()
    private var isDirty: Boolean = false

    fun isDirty(): Boolean = isDirty

    fun setIsDirty(isDirty: Boolean) {
        this.isDirty = isDirty
    }

    fun translation(): Vector3 = Vector3(translation)

    fun setTranslation(translation: Vector3) {
        this.translation = translation
        this.isDirty = true
    }

    fun rotation(): Vector3 = Vector3(rotation)

    fun setRotation(rotation: Vector3) {
        this.rotation = rotation
        this.isDirty = true
    }

    fun scale(): Vector3 = Vector3(scale)

    fun setScale(scale: Vector3) {
        this.scale = scale
        this.isDirty = true
    }

    fun matrix(): Matrix4 {
        if (isDirty) {
            recalculateTransformations()
        }

        return Matrix4(matrix)
    }

    private fun recalculateTransformations() {
        // Convert euler angles to a quaternion
        val cr = cos(rotation.x * 0.5).toFloat()
        val sr = sin(rotation.x * 0.5).toFloat()
        val cp = cos(rotation.y * 0.5).toFloat()
        val sp = sin(rotation.y * 0.5).toFloat()
        val cy = cos(rotation.z * 0.5).toFloat()
        val sy = sin(rotation.z * 0.5).toFloat()
        val w = cy * cr * cp + sy * sr * sp
        val x = cy * sr * cp - sy * cr * sp
        val y = cy * cr * sp + sy * sr * cp
        val z = sy * cr * cp - cy * sr * sp

        // Cache some data for further computations
        val x2 = x + x
        val y2 = y + y
        val z2 = z + z
        val xx = x * x2
        val xy = x * y2
        val xz = x * z2
        val yy = y * y2
        val yz = y * z2
        val zz = z * z2
        val wx = w * x2
        val wy = w * y2
        val wz = w * z2

        // Apply rotation and scale simultaneously, simply adding the translation.
        matrix[0, 0] = (1f - (yy + zz)) * scale.x
        matrix[0, 1] = (xy + wz) * scale.x
        matrix[0, 2] = (xz - wy) * scale.x
        matrix[0, 3] = translation.x
        matrix[1, 0] = (xy - wz) * scale.y
        matrix[1, 1] = (1f - (xx + zz)) * scale.y
        matrix[1, 2] = (yz + wx) * scale.y
        matrix[1, 3] = translation.y
        matrix[2, 0] = (xz + wy) * scale.z
        matrix[2, 1] = (yz - wx) * scale.z
        matrix[2, 2] = (1f - (xx + yy)) * scale.z
        matrix[2, 3] = translation.z
        matrix[3, 0] = 0f
        matrix[3, 1] = 0f
        matrix[3, 2] = 0f
        matrix[3, 3] = 1f

        isDirty = false
    }
}