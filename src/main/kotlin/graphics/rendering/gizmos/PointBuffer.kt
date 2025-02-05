package graphics.rendering.gizmos

import graphics.assets.Asset
import graphics.assets.buffer.BufferUtil
import graphics.rendering.Drawable
import org.lwjgl.opengl.GL43.*

class PointBuffer : Asset, Drawable {
    private var vaoId: Int = 0
    private var vboId: Int = 0

    init {
        create()
    }

    override fun getId(): Int {
        return vaoId
    }

    override fun create() {
        if (vaoId != 0 || vboId != 0) {
            return
        }

        vboId = glGenBuffers()
        vaoId = glGenVertexArrays()

        val vertexBuffer = BufferUtil.createFlippedBuffer(0f, 0f, 0f)
        glBindVertexArray(vaoId)
        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)

        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.SIZE_BYTES, 0)
        glBindVertexArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    override fun destroy() {
        if (vaoId != 0) {
            glDeleteVertexArrays(vaoId)
        }
        if (vboId != 0) {
            glDeleteBuffers(vboId)
        }
        vaoId = 0
        vboId = 0
    }

    override fun bind() {
        glBindVertexArray(vaoId)
        glEnableVertexAttribArray(0)
    }

    override fun unbind() {
        glBindVertexArray(0)
        glDisableVertexAttribArray(0)
    }

    override fun draw() {
        glBindVertexArray(vaoId)
        glDrawArrays(GL_POINTS, 0, 1)
        glBindVertexArray(0)
    }
}