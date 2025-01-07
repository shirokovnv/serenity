package core.math

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Quaternion {
    var x = 0f
    var y = 0f
    var z = 0f
    var w = 0f

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

    fun rotate(vector: Vector3): Vector3 {
        val qVec = Vector3(x, y, z)
        val uv = qVec.cross(vector)
        val uuv = qVec.cross(uv);
        return vector + (uv * (2f * w)) + (uuv * 2f);
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