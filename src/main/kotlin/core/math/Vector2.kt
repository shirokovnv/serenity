package core.math

import kotlin.math.sqrt

class Vector2 {
    var x = 0f
    var y = 0f

    constructor() {
        x = 0f
        y = 0f
    }

    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    constructor(vector: Vector2) {
        x = vector.x
        y = vector.y
    }

    fun length(): Float {
        return sqrt(x * x + y * y)
    }

    fun lengthSquared(): Float {
        return x * x + y * y
    }

    fun dot(vector: Vector2): Float {
        return x * vector.x + y * vector.y
    }

    fun normalize(): Vector2 {
        val length = length()
        x /= length
        y /= length
        return this
    }

    operator fun plus(vector: Vector2): Vector2 {
        return Vector2(x + vector.x, y + vector.y)
    }

    operator fun plus(scalar: Float): Vector2 {
        return Vector2(x + scalar, y + scalar)
    }

    operator fun plusAssign(vector: Vector2) {
        x += vector.x
        y += vector.y
    }

    operator fun plusAssign(scalar: Float) {
        x += scalar
        y += scalar
    }

    operator fun minus(vector: Vector2): Vector2 {
        return Vector2(x - vector.x, y - vector.y)
    }

    operator fun minus(scalar: Float): Vector2 {
        return Vector2(x - scalar, y - scalar)
    }

    operator fun minusAssign(vector: Vector2) {
        x -= vector.x
        y -= vector.y
    }

    operator fun minusAssign(scalar: Float) {
        x -= scalar
        y -= scalar
    }

    operator fun unaryMinus(): Vector2 {
        return Vector2(-x, -y)
    }

    operator fun div(vector: Vector2): Vector2 {
        return Vector2(x / vector.x, y / vector.y)
    }

    operator fun div(scalar: Float): Vector2 {
        return Vector2(x / scalar, y / scalar)
    }

    operator fun divAssign(vector: Vector2) {
        x /= vector.x
        y /= vector.y
    }

    operator fun divAssign(scalar: Float) {
        x /= scalar
        y /= scalar
    }

    operator fun times(vector: Vector2): Vector2 {
        return Vector2(x * vector.x, y * vector.y)
    }

    operator fun times(scalar: Float): Vector2 {
        return Vector2(x * scalar, y * scalar)
    }

    operator fun timesAssign(vector: Vector2) {
        x *= vector.x
        y *= vector.y
    }

    operator fun timesAssign(scalar: Float) {
        x *= scalar
        y *= scalar
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vector2) return false
        return x == other.x && y == other.y
    }

    override fun hashCode(): Int {
        return x.hashCode() * 31 + y.hashCode()
    }

    override fun toString(): String {
        return "[$x,$y]"
    }
}