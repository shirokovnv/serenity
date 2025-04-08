package modules.water.plane

import core.management.Disposable
import graphics.rendering.Drawable
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL43

class WaterPlaneBuffer : Drawable, Disposable {
    companion object {
        private val VERTICES = floatArrayOf(
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
        )

        private val INDICES = intArrayOf(
            0, 1, 2,
            2, 1, 3
        )
    }

    private var vboId: Int = 0
    private var vaoId: Int = 0
    private var eboId: Int = 0
    private var numElements: Int = 0

    init {
        create()
    }

    fun create() {
        val vertexBuffer = BufferUtils.createFloatBuffer(VERTICES.size)
        vertexBuffer.put(VERTICES).flip()

        val indicesBuffer = BufferUtils.createIntBuffer(INDICES.size)
        indicesBuffer.put(INDICES).flip()

        vaoId = GL43.glGenVertexArrays()
        GL43.glBindVertexArray(vaoId)

        vboId = GL43.glGenBuffers()
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vboId)
        GL43.glBufferData(GL43.GL_ARRAY_BUFFER, vertexBuffer, GL43.GL_STATIC_DRAW)

        GL43.glVertexAttribPointer(0, 2, GL43.GL_FLOAT, false, 0, 0)
        GL43.glEnableVertexAttribArray(0)

        eboId = GL43.glGenBuffers()
        GL43.glBindBuffer(GL43.GL_ELEMENT_ARRAY_BUFFER, eboId)
        GL43.glBufferData(GL43.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL43.GL_STATIC_DRAW)

        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, 0)
        GL43.glBindVertexArray(0)

        numElements = INDICES.size
    }

    override fun draw() {
        if (vaoId == 0) {
            println("Water plane not initialized. Call create() first.")
            return
        }

        GL43.glBindVertexArray(vaoId)
        GL43.glEnableVertexAttribArray(0)

        GL43.glDrawElements(GL43.GL_TRIANGLES, numElements, GL43.GL_UNSIGNED_INT, 0)

        GL43.glDisableVertexAttribArray(0)
        GL43.glBindVertexArray(0)
    }

    fun destroy() {
        GL43.glDeleteVertexArrays(vaoId)
        GL43.glDeleteBuffers(vboId)
        GL43.glDeleteBuffers(eboId)
        vaoId = 0
        vboId = 0
        eboId = 0
    }

    override fun dispose() {
        destroy()
    }
}