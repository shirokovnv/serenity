package modules.terrain.marching_cubes

import core.math.Vector3
import graphics.assets.Asset
import graphics.rendering.Drawable
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL43
import java.nio.FloatBuffer

class MarchingCubesBuffer : Asset, Drawable {
    companion object {
        private const val MAX_CAPACITY = 1_000_000
        private const val VERTEX_SIZE = 3 * Float.SIZE_BYTES
    }

    private var vaoId: Int = 0
    private var vboId: Int = 0
    private var vboNormalId: Int = 0
    private var numVertices: Int = 0

    private lateinit var vertexInMemoryBuffer: FloatBuffer
    private lateinit var normalInMemoryBuffer: FloatBuffer

    init {
        create()
    }

    override fun getId(): Int {
        return vaoId
    }

    override fun create() {
        if (vaoId != 0 || vboNormalId != 0 || vboId != 0) {
            return
        }

        vboId = GL43.glGenBuffers()
        vboNormalId = GL43.glGenBuffers()
        vaoId = GL43.glGenVertexArrays()

        GL43.glBindVertexArray(vaoId)

        vertexInMemoryBuffer = initializeEmptyVertexBuffer()
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vboId)
        GL43.glBufferData(GL43.GL_ARRAY_BUFFER, vertexInMemoryBuffer, GL43.GL_DYNAMIC_DRAW)

        GL43.glEnableVertexAttribArray(0)
        GL43.glVertexAttribPointer(0, 3, GL43.GL_FLOAT, false, 0, 0)

        normalInMemoryBuffer = initializeEmptyNormalBuffer()
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vboNormalId)
        GL43.glBufferData(GL43.GL_ARRAY_BUFFER, normalInMemoryBuffer, GL43.GL_DYNAMIC_DRAW)

        GL43.glEnableVertexAttribArray(1)
        GL43.glVertexAttribPointer(1, 3, GL43.GL_FLOAT, false, 0, 0)

        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, 0)
        GL43.glBindVertexArray(0)
    }

    override fun destroy() {
        if (vaoId != 0) {
            GL43.glDeleteVertexArrays(vaoId)
        }

        if (vboId != 0) {
            GL43.glDeleteBuffers(vboId)
        }

        if (vboNormalId != 0) {
            GL43.glDeleteBuffers(vboNormalId)
        }

        vaoId = 0
        vboId = 0
        vboNormalId = 0
        numVertices = 0
    }

    override fun bind() {
        GL43.glBindVertexArray(vaoId)
    }

    override fun unbind() {
        GL43.glBindVertexArray(0)
    }

    override fun draw() {
        GL43.glBindVertexArray(vaoId)

        GL43.glDrawArrays(GL43.GL_TRIANGLES, 0, numVertices)

        GL43.glBindVertexArray(0)
    }

    fun uploadData(vertices: List<Vector3>, normals: List<Vector3>) {
        // Upload vertices
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vboId)

        vertexInMemoryBuffer
            .rewind()
            .limit(MAX_CAPACITY * VERTEX_SIZE)

        numVertices = vertices.count()
        vertices.forEach { vertex ->
            vertexInMemoryBuffer.put(vertex.x)
            vertexInMemoryBuffer.put(vertex.y)
            vertexInMemoryBuffer.put(vertex.z)
        }
        vertexInMemoryBuffer.flip()

        GL43.glBufferSubData(GL43.GL_ARRAY_BUFFER, 0, vertexInMemoryBuffer)

        // Upload normals
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vboNormalId)
        normalInMemoryBuffer
            .rewind()
            .limit(MAX_CAPACITY * VERTEX_SIZE)

        normals.forEach { normal ->
            normalInMemoryBuffer.put(normal.x)
            normalInMemoryBuffer.put(normal.y)
            normalInMemoryBuffer.put(normal.z)
        }
        normalInMemoryBuffer.flip()

        GL43.glBufferSubData(GL43.GL_ARRAY_BUFFER, 0, normalInMemoryBuffer)

        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, 0)
    }

    private fun initializeEmptyVertexBuffer(): FloatBuffer {
        val vertexData = BufferUtils.createFloatBuffer(MAX_CAPACITY * VERTEX_SIZE)
        for (i in 0..<vertexData.capacity()) {
            vertexData.put(0f)
        }
        vertexData.flip()

        return vertexData
    }

    private fun initializeEmptyNormalBuffer(): FloatBuffer {
        val normalData = BufferUtils.createFloatBuffer(MAX_CAPACITY * VERTEX_SIZE)
        for (i in 0..<normalData.capacity()) {
            normalData.put(0f)
        }
        normalData.flip()

        return normalData
    }
}