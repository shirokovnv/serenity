package modules.terrain.roam.buffers

import core.math.Vector2
import graphics.assets.buffer.BufferUtil
import modules.terrain.roam.tri.TriNodeGeometry
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL43
import java.nio.FloatBuffer
import java.nio.IntBuffer

class PatchIndexBuffer : PatchBufferInterface {
    companion object {
        private val verticesCapacity = TriNodeGeometry.treeVertexCapacity
        private val indicesCapacity = TriNodeGeometry.treeIndexCapacity

        private var vBuffer: FloatBuffer? = null
        private var iBuffer: IntBuffer? = null

        private var vertexBufferReferenceCount: Int = 0
        private var vbo: Int = 0
    }

    private var ebo = 0
    private var vaoId = 0
    private var numVertices = 0
    private var numIndices = 0

    init {
        create()
    }

    override val type: PatchBufferType
        get() = PatchBufferType.TREE_VERTEX_PATCH_INDEX_BUFFER

    override fun getId(): Int {
        return vaoId
    }

    override fun create() {
        numVertices = verticesCapacity
        numIndices = indicesCapacity

        vertexBufferReferenceCount++
        if (vertexBufferReferenceCount == 1) {
            vbo = GL43.glGenBuffers()
        }

        vaoId = GL43.glGenVertexArrays()

        GL43.glBindVertexArray(vaoId)
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vbo)
        GL43.glBufferData(
            GL43.GL_ARRAY_BUFFER,
            BufferUtil.createFlippedBuffer(Array(verticesCapacity) { Vector2(0.0f, 0.0f) }),
            GL43.GL_STATIC_DRAW
        )
        GL43.glVertexAttribPointer(0, 2, GL43.GL_FLOAT, false, Float.SIZE_BYTES * 2, 0)

        ebo = GL43.glGenBuffers()
        GL43.glBindBuffer(GL43.GL_ELEMENT_ARRAY_BUFFER, ebo)
        GL43.glBufferData(
            GL43.GL_ELEMENT_ARRAY_BUFFER,
            BufferUtil.createIntBuffer(indicesCapacity),
            GL43.GL_DYNAMIC_DRAW
        )

        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, 0)
        GL43.glBindVertexArray(0)
    }

    override fun destroy() {
        GL43.glBindVertexArray(vaoId)
        if (vertexBufferReferenceCount == 0) {
            GL43.glDeleteBuffers(vbo)
        }
        vertexBufferReferenceCount--

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
        GL43.glDrawElements(GL43.GL_TRIANGLES, numIndices, GL43.GL_UNSIGNED_INT, 0)
    }

    fun uploadVerticesData(bufferData: Array<Vector2>) {
        if (vBuffer == null) {
            vBuffer = BufferUtils.createFloatBuffer(verticesCapacity * 2)
        }

        if (bufferData.size > verticesCapacity) {
            throw RuntimeException("Increase vertex buffer size!")
        }

        vBuffer!!.rewind().clear()
        for (i in bufferData.indices) {
            vBuffer!!.put(bufferData[i].x)
            vBuffer!!.put(bufferData[i].y)
        }
        vBuffer!!.flip()

        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vbo)
        GL43.glBufferSubData(GL43.GL_ARRAY_BUFFER, 0, vBuffer!!)

        numVertices = bufferData.size
    }

    fun uploadIndicesData(bufferData: Array<Int>) {
        if (iBuffer == null) {
            iBuffer = BufferUtils.createIntBuffer(indicesCapacity)
        }

        if (bufferData.size > indicesCapacity) {
            throw RuntimeException("Increase index buffer size!")
        }

        iBuffer!!.rewind().clear()
        for (i in bufferData.indices) {
            iBuffer!!.put(bufferData[i])
        }
        iBuffer!!.flip()

        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, ebo)
        GL43.glBufferSubData(GL43.GL_ARRAY_BUFFER, 0, iBuffer!!)

        numIndices = bufferData.size
    }
}