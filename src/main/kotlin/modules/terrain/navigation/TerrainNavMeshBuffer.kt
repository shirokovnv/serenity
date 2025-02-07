package modules.terrain.navigation

import graphics.assets.Asset
import graphics.assets.buffer.BufferUtil
import graphics.geometry.Mesh3d
import graphics.rendering.Drawable
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL43.*
import java.nio.IntBuffer

class TerrainNavMeshBuffer(private val mesh: Mesh3d) : Asset, Drawable {
    private var vboId: Int = 0
    private var cboId: Int = 0
    private var vaoId: Int = 0
    private var eboId: Int = 0
    private var numVertices: Int = mesh.countVertices()
    private var numIndices: Int = mesh.countIndices()

    init {
        require(mesh.countVertices() == mesh.countColors())

        create()
        populateBuffers()
    }

    override fun getId(): Int {
        return vaoId
    }

    override fun create() {
        vboId = glGenBuffers()
        cboId = glGenBuffers()
        eboId = glGenBuffers()
        vaoId = glGenVertexArrays()
    }

    override fun destroy() {
        if (vaoId != 0) {
            glDeleteVertexArrays(vaoId)
        }
        if (vboId != 0) {
            glDeleteBuffers(vboId)
        }
        if (cboId != 0) {
            glDeleteBuffers(cboId)
        }
        if (eboId != 0) {
            glDeleteBuffers(eboId)
        }
        vaoId = 0
        vboId = 0
        cboId = 0
        eboId = 0
    }

    override fun bind() {
        glBindVertexArray(vaoId)
    }

    override fun unbind() {
        glBindVertexArray(0)
    }

    override fun draw() {
        glBindVertexArray(vaoId)
        glDrawElements(GL_TRIANGLES, numIndices, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)
    }

    private fun populateBuffers() {
        glBindVertexArray(vaoId)
        glBindBuffer(GL_ARRAY_BUFFER, vboId)

        val vertexBuffer = BufferUtil.createFlippedBuffer(mesh.getVertices().toTypedArray())
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)

        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, Float.SIZE_BYTES * 3, 0)

        val indicesBuffer: IntBuffer = BufferUtils.createIntBuffer(numIndices)
        indicesBuffer.put(mesh.getIndices().toIntArray())
        indicesBuffer.flip()

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW)

        val colorBuffer = BufferUtil.createFlippedBuffer(mesh.getColors().toTypedArray())
        glBindBuffer(GL_ARRAY_BUFFER, cboId)
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW)

        glEnableVertexAttribArray(1)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, Float.SIZE_BYTES * 3, 0)

        glBindVertexArray(0)
    }
}