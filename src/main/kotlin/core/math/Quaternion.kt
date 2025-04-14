package core.math

import kotlin.math.*

class Quaternion {
    var x = 0f
    var y = 0f
    var z = 0f
    var w = 0f

    companion object {
        fun fromAxisAngle(axisX: Float, axisY: Float, axisZ: Float, angle: Float): Quaternion {
            val halfAngle = angle * 0.5f
            val s = sin(halfAngle)

            val axis = Vector3(axisX, axisY, axisZ).normalize()
            val x = axis.x * s
            val y = axis.y * s
            val z = axis.z * s
            val w = cos(halfAngle)

            return Quaternion(x, y, z, w)
        }

        fun fromEulerAngles(roll: Float, pitch: Float, yaw: Float): Quaternion {
            val cr = cos(roll * 0.5f)
            val sr = sin(roll * 0.5f)
            val cp = cos(pitch * 0.5f)
            val sp = sin(pitch * 0.5f)
            val cy = cos(yaw * 0.5f)
            val sy = sin(yaw * 0.5f)

            val qw = cy * cr * cp + sy * sr * sp
            val qx = cy * sr * cp - sy * cr * sp
            val qy = cy * cr * sp + sy * sr * cp
            val qz = sy * cr * cp - cy * sr * sp

            return Quaternion(qx, qy, qz, qw)
        }
    }

    constructor(x: Float = 0.0f, y: Float = 0.0f, z: Float = 0.0f, w: Float = 1.0f) {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
    }

    constructor(vector: Vector3, w: Float) {
        x = vector.x
        y = vector.y
        z = vector.z
        this.w = w
    }

    constructor(quaternion: Quaternion) {
        x = quaternion.x
        y = quaternion.y
        z = quaternion.z
        w = quaternion.w
    }

    fun length(): Float {
        return sqrt(x * x + y * y + z * z + w * w)
    }

    fun lengthSquared(): Float {
        return x * x + y * y + z * z + w * w
    }

    fun normalize(): Quaternion {
        val length = length()
        x /= length
        y /= length
        z /= length
        w /= length
        return this
    }

    fun conjugate(): Quaternion {
        return Quaternion(-x, -y, -z, w)
    }

    fun toEulerAngles(): Vector3 {
        val qx = this.x
        val qy = this.y
        val qz = this.z
        val qw = this.w

        val sinPitch = 2.0f * (qw * qy - qz * qx)

        val pitch: Float = if (abs(sinPitch) >= 1f) {
            if (sinPitch > 0) {
                PI.toFloat() / 2f
            } else {
                -PI.toFloat() / 2f
            }
        } else {
            asin(sinPitch)
        }

        val yaw = atan2(2.0f * (qw * qz + qx * qy), 1.0f - 2.0f * (qy * qy + qz * qz))
        val roll = atan2(2.0f * (qw * qx + qy * qz), 1.0f - 2.0f * (qx * qx + qy * qy))

        return Vector3(roll, pitch, yaw)
    }

    operator fun plus(quaternion: Quaternion): Quaternion {
        return Quaternion(
            x + quaternion.x,
            y + quaternion.y,
            z + quaternion.z,
            w + quaternion.w
        )
    }

    operator fun plus(scalar: Float): Quaternion {
        return Quaternion(
            x + scalar,
            y + scalar,
            z + scalar,
            w + scalar
        )
    }

    operator fun plusAssign(quaternion: Quaternion) {
        x += quaternion.x
        y += quaternion.y
        z += quaternion.z
        w += quaternion.w
    }

    operator fun plusAssign(scalar: Float) {
        x += scalar
        y += scalar
        z += scalar
        w += scalar
    }

    operator fun minus(quaternion: Quaternion): Quaternion {
        return Quaternion(
            x - quaternion.x,
            y - quaternion.y,
            z - quaternion.z,
            w - quaternion.w
        )
    }

    operator fun minus(scalar: Float): Quaternion {
        return Quaternion(
            x - scalar,
            y - scalar,
            z - scalar,
            w - scalar
        )
    }

    operator fun minusAssign(quaternion: Quaternion) {
        x -= quaternion.x
        y -= quaternion.y
        z -= quaternion.z
        w -= quaternion.w
    }

    operator fun minusAssign(scalar: Float) {
        x -= scalar
        y -= scalar
        z -= scalar
        w -= scalar
    }

    operator fun unaryMinus(): Quaternion {
        return Quaternion(-x, -y, -z, -w)
    }

    operator fun div(quaternion: Quaternion): Quaternion {
        return Quaternion(
            x / quaternion.x,
            y / quaternion.y,
            z / quaternion.z,
            w / quaternion.w
        )
    }

    operator fun div(scalar: Float): Quaternion {
        return Quaternion(
            x / scalar,
            y / scalar,
            z / scalar,
            w / scalar
        )
    }

    operator fun divAssign(quaternion: Quaternion) {
        x /= quaternion.x
        y /= quaternion.y
        z /= quaternion.z
        w /= quaternion.w
    }

    operator fun divAssign(scalar: Float) {
        x /= scalar
        y /= scalar
        z /= scalar
        w /= scalar
    }

    operator fun times(quaternion: Quaternion): Quaternion {
        val newW = w * quaternion.w - x * quaternion.x - y * quaternion.y - z * quaternion.z
        val newX = x * quaternion.w + w * quaternion.x + y * quaternion.z - z * quaternion.y
        val newY = y * quaternion.w + w * quaternion.y + z * quaternion.x - x * quaternion.z
        val newZ = z * quaternion.w + w * quaternion.z + x * quaternion.y - y * quaternion.x
        return Quaternion(newX, newY, newZ, newW)
    }

    operator fun times(vector: Vector3): Quaternion {
        val newW = -x * vector.x - y * vector.y - z * vector.z
        val newX = w * vector.x + y * vector.z - z * vector.y
        val newY = w * vector.y + z * vector.x - x * vector.z
        val newZ = w * vector.z + x * vector.y - y * vector.x
        return Quaternion(newX, newY, newZ, newW)
    }

    operator fun times(scalar: Float): Quaternion {
        return Quaternion(
            x * scalar,
            y * scalar,
            z * scalar,
            w * scalar
        )
    }

    operator fun timesAssign(quaternion: Quaternion) {
        val newW = w * quaternion.w - x * quaternion.x - y * quaternion.y - z * quaternion.z
        val newX = x * quaternion.w + w * quaternion.x + y * quaternion.z - z * quaternion.y
        val newY = y * quaternion.w + w * quaternion.y + z * quaternion.x - x * quaternion.z
        val newZ = z * quaternion.w + w * quaternion.z + x * quaternion.y - y * quaternion.x

        x = newX
        y = newY
        z = newZ
        w = newW
    }

    operator fun timesAssign(vector: Vector3) {
        val newW = -x * vector.x - y * vector.y - z * vector.z
        val newX = w * vector.x + y * vector.z - z * vector.y
        val newY = w * vector.y + z * vector.x - x * vector.z
        val newZ = w * vector.z + x * vector.y - y * vector.x

        x = newX
        y = newY
        z = newZ
        w = newW
    }

    operator fun timesAssign(scalar: Float) {
        x *= scalar
        y *= scalar
        z *= scalar
        w *= scalar
    }

    fun xyz(): Vector3 {
        return Vector3(x, y, z)
    }

    operator fun component1() = x
    operator fun component2() = y
    operator fun component3() = z
    operator fun component4() = w

    operator fun get(index: Int): Float =
        when (index) {
            0 -> x
            1 -> y
            2 -> z
            3 -> w
            else -> throw IndexOutOfBoundsException()
        }

    operator fun set(index: Int, value: Float) =
        when (index) {
            0 -> x = value
            1 -> y = value
            2 -> z = value
            3 -> w = value
            else -> throw IndexOutOfBoundsException()
        }

    fun dot(other: Quaternion): Float {
        return (this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w)
    }

    fun rotate(vector: Vector3): Vector3 {
        val qVec = Vector3(x, y, z)
        val uv = qVec.cross(vector)
        val uuv = qVec.cross(uv)
        return vector + (uv * (2f * w)) + (uuv * 2f)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Quaternion) return false

        return x == other.x && y == other.y && z == other.z && w == other.w
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        result = 31 * result + w.hashCode()
        return result
    }

    override fun toString(): String {
        return "[$x,$y,$z,$w]"
    }
}

fun Quaternion.toFloatArray(): FloatArray {
    return arrayOf(x, y, z, w).toFloatArray()
}
