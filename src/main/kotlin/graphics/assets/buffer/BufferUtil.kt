package graphics.assets.buffer

import core.math.Matrix4
import core.math.Quaternion
import core.math.Vector2
import core.math.Vector3
import org.lwjgl.BufferUtils
import java.nio.ByteBuffer
import java.nio.DoubleBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

object BufferUtil {
    fun createFloatBuffer(size: Int): FloatBuffer {
        return BufferUtils.createFloatBuffer(size)
    }

    fun createIntBuffer(size: Int): IntBuffer {
        return BufferUtils.createIntBuffer(size)
    }

    fun createDoubleBuffer(size: Int): DoubleBuffer {
        return BufferUtils.createDoubleBuffer(size)
    }

    fun createFlippedBuffer(vararg values: Int): IntBuffer {
        val buffer = createIntBuffer(values.size)
        buffer.put(values)
        buffer.flip()
        return buffer
    }

    fun createFlippedBuffer(vararg values: Float): FloatBuffer {
        val buffer = createFloatBuffer(values.size)
        buffer.put(values)
        buffer.flip()
        return buffer
    }

    fun createFlippedBuffer(vararg values: Double): DoubleBuffer {
        val buffer = createDoubleBuffer(values.size)
        buffer.put(values)
        buffer.flip()
        return buffer
    }

    fun createFlippedBuffer(vector: Array<Vector3>): FloatBuffer {
        val buffer = createFloatBuffer(vector.size * java.lang.Float.BYTES * 3)
        for (i in vector.indices) {
            buffer.put(vector[i].x)
            buffer.put(vector[i].y)
            buffer.put(vector[i].z)
        }
        buffer.flip()
        return buffer
    }

    fun createFlippedBuffer(vector: Array<Quaternion>): FloatBuffer {
        val buffer = createFloatBuffer(vector.size * java.lang.Float.BYTES * 4)
        for (i in vector.indices) {
            buffer.put(vector[i].x)
            buffer.put(vector[i].y)
            buffer.put(vector[i].z)
            buffer.put(vector[i].w)
        }
        buffer.flip()
        return buffer
    }

    fun createFlippedBuffer(vector: Vector3): FloatBuffer {
        val buffer = createFloatBuffer(java.lang.Float.BYTES * 3)
        buffer.put(vector.x)
        buffer.put(vector.y)
        buffer.put(vector.z)
        buffer.flip()
        return buffer
    }

    fun createFlippedBuffer(vector: Vector2): FloatBuffer {
        val buffer = createFloatBuffer(java.lang.Float.BYTES * 2)
        buffer.put(vector.x)
        buffer.put(vector.y)
        buffer.flip()
        return buffer
    }

    fun createFlippedBuffer(vector: Quaternion): FloatBuffer {
        val buffer = createFloatBuffer(java.lang.Float.BYTES * 4)
        buffer.put(vector.x)
        buffer.put(vector.y)
        buffer.put(vector.z)
        buffer.put(vector.w)
        buffer.flip()
        return buffer
    }

    fun createFlippedBuffer(vector: Array<Vector2>): FloatBuffer {
        val buffer = createFloatBuffer(vector.size * java.lang.Float.BYTES * 2)
        for (i in vector.indices) {
            buffer.put(vector[i].x)
            buffer.put(vector[i].y)
        }
        buffer.flip()
        return buffer
    }

    fun createFlippedBuffer(matrix: Matrix4): FloatBuffer {
        val buffer = createFloatBuffer(4 * 4)
        for (i in 0..3) for (j in 0..3) buffer.put(matrix[i, j])
        buffer.flip()
        return buffer
    }

    fun createFlippedBuffer(matrices: Array<Matrix4>): FloatBuffer {
        val buffer = createFloatBuffer(4 * 4 * matrices.size)
        for (matrix in matrices) {
            for (i in 0..3) for (j in 0..3) buffer.put(matrix[i, j])
        }
        buffer.flip()
        return buffer
    }

    fun resizeBuffer(buffer: ByteBuffer, newCapacity: Int): ByteBuffer {
        val newBuffer = BufferUtils.createByteBuffer(newCapacity)
        buffer.flip()
        newBuffer.put(buffer)
        return newBuffer
    }
}