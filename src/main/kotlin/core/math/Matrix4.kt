package core.math

import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer

class Matrix4(other: Matrix4? = null) {
    var m: Array<FloatArray> = Array(4) { FloatArray(4) }

    init {
        if (other != null) {
            for (i in 0..3) {
                for (j in 0..3) {
                    this[i, j] = other[i, j]
                }
            }
        }
    }

    fun zero(): Matrix4 {
        for (i in 0..3) {
            for (j in 0..3) {
                this[i, j] = 0f
            }
        }
        return this
    }

    fun identity(): Matrix4 {
        for (i in 0..3) {
            for (j in 0..3) {
                this[i, j] = if (i == j) 1f else 0f
            }
        }
        return this
    }

    fun transpose(): Matrix4 {
        val result = Matrix4()
        for (i in 0..3) {
            for (j in 0..3) {
                result[i, j] = get(j, i)
            }
        }
        return result
    }

    fun invert(): Matrix4 {
        val s0 = get(0, 0) * get(1, 1) - get(1, 0) * get(0, 1)
        val s1 = get(0, 0) * get(1, 2) - get(1, 0) * get(0, 2)
        val s2 = get(0, 0) * get(1, 3) - get(1, 0) * get(0, 3)
        val s3 = get(0, 1) * get(1, 2) - get(1, 1) * get(0, 2)
        val s4 = get(0, 1) * get(1, 3) - get(1, 1) * get(0, 3)
        val s5 = get(0, 2) * get(1, 3) - get(1, 2) * get(0, 3)
        val c5 = get(2, 2) * get(3, 3) - get(3, 2) * get(2, 3)
        val c4 = get(2, 1) * get(3, 3) - get(3, 1) * get(2, 3)
        val c3 = get(2, 1) * get(3, 2) - get(3, 1) * get(2, 2)
        val c2 = get(2, 0) * get(3, 3) - get(3, 0) * get(2, 3)
        val c1 = get(2, 0) * get(3, 2) - get(3, 0) * get(2, 2)
        val c0 = get(2, 0) * get(3, 1) - get(3, 0) * get(2, 1)
        val div = s0 * c5 - s1 * c4 + s2 * c3 + s3 * c2 - s4 * c1 + s5 * c0

        if (div == 0f) {
            throw IllegalStateException("Not invertible")
        }

        val invDet = 1.0f / div
        val invM = Matrix4()
        invM[0, 0] = (get(1, 1) * c5 - get(1, 2) * c4 + get(1, 3) * c3) * invDet
        invM[0, 1] = (-get(0, 1) * c5 + get(0, 2) * c4 - get(0, 3) * c3) * invDet
        invM[0, 2] = (get(3, 1) * s5 - get(3, 2) * s4 + get(3, 3) * s3) * invDet
        invM[0, 3] = (-get(2, 1) * s5 + get(2, 2) * s4 - get(2, 3) * s3) * invDet
        invM[1, 0] = (-get(1, 0) * c5 + get(1, 2) * c2 - get(1, 3) * c1) * invDet
        invM[1, 1] = (get(0, 0) * c5 - get(0, 2) * c2 + get(0, 3) * c1) * invDet
        invM[1, 2] = (-get(3, 0) * s5 + get(3, 2) * s2 - get(3, 3) * s1) * invDet
        invM[1, 3] = (get(2, 0) * s5 - get(2, 2) * s2 + get(2, 3) * s1) * invDet
        invM[2, 0] = (get(1, 0) * c4 - get(1, 1) * c2 + get(1, 3) * c0) * invDet
        invM[2, 1] = (-get(0, 0) * c4 + get(0, 1) * c2 - get(0, 3) * c0) * invDet
        invM[2, 2] = (get(3, 0) * s4 - get(3, 1) * s2 + get(3, 3) * s0) * invDet
        invM[2, 3] = (-get(2, 0) * s4 + get(2, 1) * s2 - get(2, 3) * s0) * invDet
        invM[3, 0] = (-get(1, 0) * c3 + get(1, 1) * c1 - get(1, 2) * c0) * invDet
        invM[3, 1] = (get(0, 0) * c3 - get(0, 1) * c1 + get(0, 2) * c0) * invDet
        invM[3, 2] = (-get(3, 0) * s3 + get(3, 1) * s1 - get(3, 2) * s0) * invDet
        invM[3, 3] = (get(2, 0) * s3 - get(2, 1) * s1 + get(2, 2) * s0) * invDet
        return invM
    }

    operator fun plus(m: Matrix4): Matrix4 {
        val res = Matrix4()
        for (i in 0..3) {
            for (j in 0..3) {
                res[i, j] = this.m[i][j] + m[i, j]
            }
        }
        return res
    }

    operator fun minus(m: Matrix4): Matrix4 {
        val res = Matrix4()
        for (i in 0..3) {
            for (j in 0..3) {
                res[i, j] = this.m[i][j] - m[i, j]
            }
        }
        return res
    }

    operator fun times(m: Matrix4): Matrix4 {
        val res = Matrix4()
        for (i in 0..3) {
            for (j in 0..3) {
                res[i, j] = this.m[i][0] * m[0, j] + this.m[i][1] * m[1, j] + this.m[i][2] * m[2, j] + this.m[i][3] * m[3, j]
            }
        }
        return res
    }

    operator fun times(v: Quaternion): Quaternion {
        val res = Quaternion(0f, 0f, 0f, 0f)
        res.x = m[0][0] * v.x + m[0][1] * v.y + m[0][2] * v.z + m[0][3] * v.w
        res.y = m[1][0] * v.x + m[1][1] * v.y + m[1][2] * v.z + m[1][3] * v.w
        res.z = m[2][0] * v.x + m[2][1] * v.y + m[2][2] * v.z + m[2][3] * v.w
        res.w = m[3][0] * v.x + m[3][1] * v.y + m[3][2] * v.z + m[3][3] * v.w
        return res
    }

    operator fun set(x: Int, y: Int, value: Float) {
        m[x][y] = value
    }

    operator fun get(x: Int, y: Int): Float {
        return m[x][y]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix4) return false
        for (i in 0..3) {
            for (j in 0..3) {
                if (this.m[i][j] != other.m[i][j]) return false
            }
        }
        return true
    }

    override fun hashCode(): Int {
        var result = 17
        for (i in 0..3) {
            for (j in 0..3) {
                result = 31 * result + m[i][j].hashCode()
            }
        }
        return result
    }

    override fun toString(): String {
        return """|${m[0][0]} ${m[0][1]} ${m[0][2]} ${m[0][3]}|
            |${m[1][0]} ${m[1][1]} ${m[1][2]} ${m[1][3]}|
            |${m[2][0]} ${m[2][1]} ${m[2][2]} ${m[2][3]}|
            |${m[3][0]} ${m[3][1]} ${m[3][2]} ${m[3][3]}|"""
    }
}

fun Matrix4.toFloatBuffer(): FloatBuffer {
    val buffer = MemoryUtil.memAllocFloat(16)
    for (i in 0..3) {
        for (j in 0..3) {
            buffer.put(this[i, j])
        }
    }
    buffer.flip()

    return buffer
}