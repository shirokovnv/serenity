package modules.light.flare

import graphics.assets.Asset
import graphics.assets.buffer.BufferUtil
import graphics.rendering.Drawable
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL43.*

class LensFlareBuffer(private val flareMesh: LensFlareMesh): Asset, Drawable {
    private var vboId: Int = 0
    private var vaoId: Int = 0
    private var numVertices: Int = 0

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
    }

    override fun destroy() {
        glBindVertexArray(vaoId)
        GL15.glDeleteBuffers(vboId)
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
        numVertices = flareMesh.countVertices()

        glBindVertexArray(vaoId)

        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, BufferUtil.createFlippedBuffer(flareMesh.getVertices().toTypedArray()), GL_STATIC_DRAW)

        glVertexAttribPointer(0, 2, GL_FLOAT, false, Float.SIZE_BYTES * 2, 0)
        glEnableVertexAttribArray(0)

        glBindVertexArray(0)
    }

    override fun draw() {
        glBindVertexArray(vaoId)
        glEnableVertexAttribArray(0)

        glDrawArrays(GL_TRIANGLE_STRIP, 0, numVertices)

        glDisableVertexAttribArray(0)
        glBindVertexArray(0)
    }
}