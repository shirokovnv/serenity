package graphics.rendering.postproc

import core.management.Disposable
import core.management.Resources
import graphics.rendering.Drawable
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL43.*

typealias ScreenQuadProvider = () -> ScreenQuad

class ScreenQuad : Drawable, Disposable {
    companion object {
        private val VERTICES = floatArrayOf(
            -1.0f, 1.0f,
            1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f,
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

        vaoId = glGenVertexArrays()
        glBindVertexArray(vaoId)

        vboId = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW)

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0)
        glEnableVertexAttribArray(0)

        eboId = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)

        numElements = INDICES.size
    }

    override fun draw() {
        if (vaoId == 0) {
            println("Quad not initialized. Call create() first.")
            return
        }

        glBindVertexArray(vaoId)
        glEnableVertexAttribArray(0)

        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_INT, 0)

        glDisableVertexAttribArray(0)
        glBindVertexArray(0)
    }

    fun destroy() {
        glDeleteVertexArrays(vaoId)
        glDeleteBuffers(vboId)
        glDeleteBuffers(eboId)
        vaoId = 0
        vboId = 0
        eboId = 0
    }

    override fun dispose() {
        destroy()
    }
}

fun defaultScreenQuadProvider(): ScreenQuad = Resources.get<ScreenQuad>() ?: ScreenQuad()