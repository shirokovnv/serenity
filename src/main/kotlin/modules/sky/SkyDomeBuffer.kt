package modules.sky

import graphics.assets.Asset
import graphics.rendering.Drawable
import org.lwjgl.opengl.GL43.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

class SkyDomeBuffer(private val skyDomeMesh: SkyDomeMesh): Asset, Drawable {
    private var vboId: Int = 0
    private var vaoId: Int = 0
    private var numVertices: Int = 0

    companion object {
        private val valueBuffer: IntBuffer = ByteBuffer.allocateDirect(Int.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer()

        fun getCurrentDepthFunc(): Int {
            glGetIntegerv(GL_DEPTH_FUNC, valueBuffer)
            return valueBuffer.get(0)
        }
    }

    init {
        numVertices = skyDomeMesh.countVertices()

        create()
        populateBuffers()
    }

    override fun draw() {
        val cacheDepthFuncMode = getCurrentDepthFunc()
        glDepthFunc(GL_LEQUAL)

        glBindVertexArray(vaoId)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)

        glDrawArrays(GL_TRIANGLES, 0, numVertices)

        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glBindVertexArray(0)

        glDepthFunc(cacheDepthFuncMode)
    }

    override fun getId(): Int {
        return vaoId
    }

    override fun create() {
        vboId = glGenBuffers()
        vaoId = glGenVertexArrays()
    }

    override fun destroy() {
        glBindVertexArray(vaoId)
        glDeleteBuffers(vboId)
        glDeleteVertexArrays(vaoId)
        glBindVertexArray(0)
    }

    override fun bind() {
        glBindVertexArray(vaoId)
    }

    override fun unbind() {
        glBindVertexArray(0)
    }

    private fun populateBuffers() {
        glBindVertexArray(vaoId)
        glBindBuffer(GL_ARRAY_BUFFER, vboId)

        val vertices = skyDomeMesh.getVertices()
        val uvs = skyDomeMesh.getUVs()

        val vertexData = ByteBuffer.allocateDirect(numVertices * 5 * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

        for (i in 0..<numVertices) {
            vertexData.put(vertices[i].x)
            vertexData.put(vertices[i].y)
            vertexData.put(vertices[i].z)
            vertexData.put(uvs[i].x)
            vertexData.put(uvs[i].y)
        }
        vertexData.flip()
        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW)

        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, Float.SIZE_BYTES * 5, 0)

        glEnableVertexAttribArray(1)
        glVertexAttribPointer(1, 2, GL_FLOAT, false, Float.SIZE_BYTES * 5, Float.SIZE_BYTES * 3L)

        glBindVertexArray(0)
    }
}