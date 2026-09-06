package modules.terrain.roam.buffers

import core.math.Vector2
import graphics.assets.buffer.BufferUtil
import modules.terrain.roam.tri.TriNodeGeometry
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL43
import java.nio.FloatBuffer

class PatchVertexBuffer : PatchBufferInterface {
    companion object {
        private val capacity = TriNodeGeometry.maxLeafTriangles * TriNodeGeometry.vertexPerTriangle
        private var buffer: FloatBuffer? = null
    }

    private var vbo = 0
    private var vaoId = 0
    private var size = 0

    init {
        create()
    }

    override fun getId(): Int {
        return vaoId
    }

    override fun create() {
        size = capacity
        vbo = GL43.glGenBuffers()
        vaoId = GL43.glGenVertexArrays()

        GL43.glBindVertexArray(vaoId)
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vbo)
        GL43.glBufferData(
            GL43.GL_ARRAY_BUFFER,
            BufferUtil.createFlippedBuffer(Array(capacity) { Vector2(0.0f, 0.0f) }),
            GL43.GL_DYNAMIC_DRAW
        )
        GL43.glVertexAttribPointer(0, 2, GL43.GL_FLOAT, false, Float.SIZE_BYTES * 2, 0)
        GL43.glBindVertexArray(0)
    }

    override fun destroy() {
        GL43.glBindVertexArray(vaoId)
        GL43.glDeleteBuffers(vbo)
        GL43.glDeleteVertexArrays(vaoId)
        GL43.glBindVertexArray(0)
    }

    override fun bind() {
        GL43.glBindVertexArray(vaoId)
        GL43.glEnableVertexAttribArray(0)
    }

    override fun unbind() {
        GL43.glDisableVertexAttribArray(0)
        GL43.glBindVertexArray(0)
    }

    override fun draw() {
        GL43.glDrawArrays(GL43.GL_TRIANGLES, 0, size)
    }


    fun uploadData(bufferData: Array<Vector2>) {
        if (buffer == null) {
            buffer = BufferUtils.createFloatBuffer(capacity * 2)
        }

        if (bufferData.size > capacity) {
            throw RuntimeException("Increase vertex buffer size!")
        }

        buffer!!.rewind().clear()
        for (i in bufferData.indices) {
            buffer!!.put(bufferData[i].x)
            buffer!!.put(bufferData[i].y)
        }
        buffer!!.flip()

        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vbo)
        GL43.glBufferSubData(GL43.GL_ARRAY_BUFFER, 0, buffer!!)

        size = bufferData.size
    }

    fun uploadData(bufferData: FloatArray) {
        if (bufferData.size > capacity) {
            throw RuntimeException("Increase vertex buffer size!")
        }

        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vbo)
        GL43.glBufferSubData(GL43.GL_ARRAY_BUFFER, 0, bufferData)

        size = bufferData.size / 2
    }
}
