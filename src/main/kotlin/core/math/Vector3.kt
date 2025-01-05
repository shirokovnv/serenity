package core.math

import kotlin.math.sqrt

class Vector3 {
    var x = 0f
    var y = 0f
    var z = 0f

    constructor() {
        x = 0f
        y = 0f
        z = 0f
    }

    constructor(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    constructor(x: Float) {
        this.x = x
        y = x
        z = x
    }

    constructor(vector: Vector3) {
        x = vector.x
        y = vector.y
        z = vector.z
    }

    fun length(): Float {
        return sqrt(x * x + y * y + z * z)
    }

    fun lengthSquared(): Float {
        return x * x + y * y + z * z
    }

    fun dot(vector: Vector3): Float {
        return x * vector.x + y * vector.y + z * vector.z
    }

    fun cross(vector: Vector3): Vector3 {
        val x = y * vector.z - z * vector.y
        val y = z * vector.x - this.x * vector.z
        val z = this.x * vector.y - this.y * vector.x
        return Vector3(x, y, z)
    }

    fun normalize(): Vector3 {
        val length = length()
        x /= length
        y /= length
        z /= length
        return this
    }

    operator fun plus(vector: Vector3): Vector3 {
        return Vector3(x + vector.x, y + vector.y, z + vector.z)
    }

    operator fun plus(scalar: Float): Vector3 {
        return Vector3(x + scalar, y + scalar, z + scalar)
    }

    operator fun plusAssign(vector: Vector3) {
        x += vector.x
        y += vector.y
        z += vector.z
    }

    operator fun plusAssign(scalar: Float) {
        x += scalar
        y += scalar
        z += scalar
    }

    operator fun minus(vector: Vector3): Vector3 {
        return Vector3(x - vector.x, y - vector.y, z - vector.z)
    }

    operator fun minus(scalar: Float): Vector3 {
        return Vector3(x - scalar, y - scalar, z - scalar)
    }

    operator fun minusAssign(vector: Vector3) {
        x -= vector.x
        y -= vector.y
        z -= vector.z
    }

    operator fun minusAssign(scalar: Float) {
        x -= scalar
        y -= scalar
        z -= scalar
    }

    operator fun unaryMinus(): Vector3 {
        x = -x
        y = -y
        z = -z

        return this
    }

    operator fun div(vector: Vector3): Vector3 {
        return Vector3(x / vector.x, y / vector.y, z / vector.z)
    }

    operator fun div(scalar: Float): Vector3 {
        return Vector3(x / scalar, y / scalar, z / scalar)
    }

    operator fun divAssign(vector: Vector3) {
        x /= vector.x
        y /= vector.y
        z /= vector.z
    }

    operator fun divAssign(scalar: Float) {
        x /= scalar
        y /= scalar
        z /= scalar
    }

    operator fun times(vector: Vector3): Vector3 {
        return Vector3(x * vector.x, y * vector.y, z * vector.z)
    }

    operator fun times(scalar: Float): Vector3 {
        return Vector3(x * scalar, y * scalar, z * scalar)
    }

    operator fun timesAssign(vector: Vector3) {
        x *= vector.x
        y *= vector.y
        z *= vector.z
    }

    operator fun timesAssign(scalar: Float) {
        x *= scalar
        y *= scalar
        z *= scalar
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vector3) return false

        return x == other.x && y == other.y && z == other.z
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    override fun toString(): String {
        return "[$x,$y,$z]"
    }
}