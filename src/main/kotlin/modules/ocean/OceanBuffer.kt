package modules.ocean

import graphics.assets.Asset
import graphics.rendering.Drawable
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL43.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class OceanBuffer(private val oceanMesh: OceanMesh): Asset, Drawable {
    private var vboId: Int = 0
    private var vaoId: Int = 0
    private var eboId: Int = 0
    private var numVertices: Int = 0
    private var numIndices: Int = 0

    init {
        numVertices = oceanMesh.countVertices()
        numIndices = oceanMesh.countIndices()

        create()
        populateBuffers()
    }

    override fun draw() {
        glBindVertexArray(vaoId)
        glDrawElements(GL_TRIANGLES, numIndices, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)
    }

    override fun getId(): Int {
        return vaoId
    }

    override fun create() {
        vboId = glGenBuffers()
        vaoId = glGenVertexArrays()
    }

    override fun destroy() {
        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glDisableVertexAttribArray(2)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glDeleteBuffers(vboId)
        glDeleteBuffers(eboId)

        glBindVertexArray(0)
        glDeleteVertexArrays(vaoId)
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

        val vertices = oceanMesh.getVertices()
        val uvs = oceanMesh.getUVs()
        val normals = oceanMesh.getNormals()
        val indices = oceanMesh.getIndices()

        // VERTICES
        val vertexData = ByteBuffer.allocateDirect(numVertices * 8 * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

        for (i in 0..<numVertices) {
            vertexData.put(vertices[i].x)
            vertexData.put(vertices[i].y)
            vertexData.put(vertices[i].z)
            vertexData.put(uvs[i].x)
            vertexData.put(uvs[i].y)
            vertexData.put(normals[i].x)
            vertexData.put(normals[i].y)
            vertexData.put(normals[i].z)
        }
        vertexData.flip()

        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW)

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.SIZE_BYTES, 0)
        glEnableVertexAttribArray(0)

        glVertexAttribPointer(1, 2, GL_FLOAT, false, 8 * Float.SIZE_BYTES, (3 * Float.SIZE_BYTES).toLong())
        glEnableVertexAttribArray(1)

        glVertexAttribPointer(2, 3, GL_FLOAT, false, 8 * Float.SIZE_BYTES, (5 * Float.SIZE_BYTES).toLong())
        glEnableVertexAttribArray(2)

        // INDICES
        val indexData = BufferUtils.createIntBuffer(indices.size)
        indexData.put(indices.toIntArray()).flip()

        eboId = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData, GL_STATIC_DRAW)

        glBindVertexArray(0)
    }
}