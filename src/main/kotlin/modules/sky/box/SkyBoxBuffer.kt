package modules.sky.box

import core.math.Vector3
import graphics.assets.Asset
import graphics.assets.buffer.BufferUtil
import graphics.rendering.Drawable
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL43.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

class SkyBoxBuffer : Asset, Drawable {

    companion object {
        private val vertices: Array<Vector3> = arrayOf(
            Vector3(-1.0f, 1.0f, -1.0f),
            Vector3(-1.0f, -1.0f, -1.0f),
            Vector3(1.0f, -1.0f, -1.0f),
            Vector3(1.0f, -1.0f, -1.0f),
            Vector3(1.0f, 1.0f, -1.0f),
            Vector3(-1.0f, 1.0f, -1.0f),

            Vector3(-1.0f, -1.0f, 1.0f),
            Vector3(-1.0f, -1.0f, -1.0f),
            Vector3(-1.0f, 1.0f, -1.0f),
            Vector3(-1.0f, 1.0f, -1.0f),
            Vector3(-1.0f, 1.0f, 1.0f),
            Vector3(-1.0f, -1.0f, 1.0f),

            Vector3(1.0f, -1.0f, -1.0f),
            Vector3(1.0f, -1.0f, 1.0f),
            Vector3(1.0f, 1.0f, 1.0f),
            Vector3(1.0f, 1.0f, 1.0f),
            Vector3(1.0f, 1.0f, -1.0f),
            Vector3(1.0f, -1.0f, -1.0f),

            Vector3(-1.0f, -1.0f, 1.0f),
            Vector3(-1.0f, 1.0f, 1.0f),
            Vector3(1.0f, 1.0f, 1.0f),
            Vector3(1.0f, 1.0f, 1.0f),
            Vector3(1.0f, -1.0f, 1.0f),
            Vector3(-1.0f, -1.0f, 1.0f),

            Vector3(-1.0f, 1.0f, -1.0f),
            Vector3(1.0f, 1.0f, -1.0f),
            Vector3(1.0f, 1.0f, 1.0f),
            Vector3(1.0f, 1.0f, 1.0f),
            Vector3(-1.0f, 1.0f, 1.0f),
            Vector3(-1.0f, 1.0f, -1.0f),

            Vector3(-1.0f, -1.0f, -1.0f),
            Vector3(-1.0f, -1.0f, 1.0f),
            Vector3(1.0f, -1.0f, -1.0f),
            Vector3(1.0f, -1.0f, -1.0f),
            Vector3(-1.0f, -1.0f, 1.0f),
            Vector3(1.0f, -1.0f, 1.0f)
        )

        private const val STRIDE = 3 * Float.SIZE_BYTES

        private val valueBuffer: IntBuffer = ByteBuffer.allocateDirect(Int.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer()

        fun getCurrentDepthFunc(): Int {
            glGetIntegerv(GL_DEPTH_FUNC, valueBuffer)
            return valueBuffer.get(0)
        }

        fun getCurrentCullFaceMode(): Int {
            GL11.glGetIntegerv(GL_CULL_FACE_MODE, valueBuffer)
            return valueBuffer.get(0)
        }
    }

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

        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBindVertexArray(vaoId)

        val vertexData = BufferUtil.createFlippedBuffer(vertices)
        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW)

        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, STRIDE, 0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
    }

    override fun destroy() {
        if (vaoId != 0) {
            glDeleteVertexArrays(vaoId)
            vaoId = 0
        }

        if (vboId != 0) {
            glDeleteBuffers(vboId)
            vboId = 0
        }
    }

    override fun bind() {
        glBindVertexArray(vaoId)
    }

    override fun unbind() {
        glBindVertexArray(0)
    }

    override fun draw() {
        val cachedCullFaceMode = getCurrentCullFaceMode()
        val cachedDepthFuncMode = getCurrentDepthFunc()

        glCullFace(GL_FRONT)
        glDepthFunc(GL_LEQUAL)

        glBindVertexArray(vaoId)

        glDrawArrays(GL_TRIANGLES, 0, 36)

        glBindVertexArray(0)

        glCullFace(cachedCullFaceMode)
        glDepthFunc(cachedDepthFuncMode)
    }
}