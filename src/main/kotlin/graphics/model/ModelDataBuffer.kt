package graphics.model

import graphics.assets.Asset
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL43.*
import java.nio.IntBuffer

class ModelDataBuffer(private val modelData: ModelData) : Asset {
    private var vaoId: Int = 0
    private var vboId: Int = 0
    private var eboId: Int = 0
    private var numVertices: Int = 0
    private var numIndices: Int = 0

    init {
        create()
        populateBuffers()
    }

    override fun getId(): Int {
        return vaoId
    }

    override fun create() {
        vboId = glGenBuffers()
        vaoId = glGenVertexArrays()
        eboId = glGenBuffers()
    }

    override fun destroy() {
        if (vaoId != 0) {
            glDeleteVertexArrays(vaoId)
        }
        if (vboId != 0) {
            glDeleteBuffers(vboId)
        }
        if (eboId != 0) {
            glDeleteBuffers(eboId)
        }
        vaoId = 0
        vboId = 0
        eboId = 0
    }

    override fun bind() {
        glBindVertexArray(vaoId)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
        glEnableVertexAttribArray(2)
    }

    override fun unbind() {
        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glDisableVertexAttribArray(2)
        glBindVertexArray(0)
    }

    private fun populateBuffers() {
        val vertices = modelData.vertices
        val textures = modelData.textureCoordinates
        val normals = modelData.normals
        val indices = modelData.indices

        numVertices = vertices.size / 3
        numIndices = indices.size

        val vertexBuffer = BufferUtils.createFloatBuffer(vertices.size + normals.size + textures.size)
        for ((index, i) in (vertices.indices step 3).withIndex()) {

            vertexBuffer.put(vertices[i])
            vertexBuffer.put(vertices[i + 1])
            vertexBuffer.put(vertices[i + 2])

            vertexBuffer.put(normals[i])
            vertexBuffer.put(normals[i + 1])
            vertexBuffer.put(normals[i + 2])

            vertexBuffer.put(textures[index * 2])
            vertexBuffer.put(textures[index * 2 + 1])
        }
        vertexBuffer.flip()

        glBindVertexArray(vaoId)
        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)

        val indicesBuffer: IntBuffer = BufferUtils.createIntBuffer(indices.size)
        indicesBuffer.put(indices)
        indicesBuffer.flip()

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW)

        glVertexAttribPointer(0, 3, GL_FLOAT, false, Float.SIZE_BYTES * 8, 0)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, Float.SIZE_BYTES * 8, (Float.SIZE_BYTES * 3L))
        glVertexAttribPointer(2, 2, GL_FLOAT, false, Float.SIZE_BYTES * 8, (Float.SIZE_BYTES * 6L))

        glBindVertexArray(0)
    }
}