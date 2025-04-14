package graphics.particles

import core.math.Quaternion
import graphics.assets.Asset
import graphics.rendering.Drawable
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL43
import java.nio.FloatBuffer

class ParticleBatchBuffer(
    private val capacity: Int
) : Asset, Drawable {

    private var vaoId: Int = 0
    private var vboId: Int = 0
    private var numVertices: Int = 0

    private lateinit var vertexInMemoryBuffer: FloatBuffer

    private val vertexSize = 3
    private val colorSize = 4
    private val scaleSize = 1
    private val stride = Float.SIZE_BYTES * (vertexSize + colorSize + scaleSize)

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

        vaoId = GL43.glGenVertexArrays()
        GL43.glBindVertexArray(vaoId)

        vboId = GL43.glGenBuffers()
        vertexInMemoryBuffer = initializeEmptyVertexBuffer()
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vboId)
        GL43.glBufferData(GL43.GL_ARRAY_BUFFER, vertexInMemoryBuffer, GL43.GL_DYNAMIC_DRAW)

        GL43.glVertexAttribPointer(0, vertexSize, GL43.GL_FLOAT, false, stride, 0)
        GL43.glVertexAttribPointer(
            1,
            colorSize,
            GL43.GL_FLOAT,
            false,
            stride,
            ((vertexSize) * Float.SIZE_BYTES).toLong()
        )
        GL43.glVertexAttribPointer(
            2,
            scaleSize,
            GL43.GL_FLOAT,
            false,
            stride,
            ((vertexSize + colorSize) * Float.SIZE_BYTES).toLong()
        )

        GL43.glBindVertexArray(0)
    }

    override fun destroy() {
        if (vaoId != 0) {
            GL43.glDeleteVertexArrays(vaoId)
        }

        if (vboId != 0) {
            GL43.glDeleteBuffers(vboId)
        }

        if (::vertexInMemoryBuffer.isInitialized) {
            vertexInMemoryBuffer.clear()
        }

        vaoId = 0
        vboId = 0
        numVertices = 0
    }

    override fun bind() {
        GL43.glBindVertexArray(vaoId)
    }

    override fun unbind() {
        GL43.glBindVertexArray(0)
    }

    fun uploadData(particles: Iterable<Particle>) {
        // Upload vertices and texture coordinates
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vboId)

        vertexInMemoryBuffer
            .rewind()
            .limit(capacity * stride)
        numVertices = particles.count()
        particles.forEach { particle ->
            val vertexPosition =
                (particle.transform.matrix() * Quaternion(particle.position, 1f)).xyz()

            for (j in 0..<vertexSize) {
                vertexInMemoryBuffer.put(vertexPosition[j])
            }

            for (j in 0..<colorSize) {
                vertexInMemoryBuffer.put(particle.color[j])
            }

            vertexInMemoryBuffer.put(particle.scale)
        }
        vertexInMemoryBuffer.flip()

        GL43.glBufferSubData(GL43.GL_ARRAY_BUFFER, 0, vertexInMemoryBuffer)
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, 0)
    }

    override fun draw() {
        GL43.glBindVertexArray(vaoId)
        GL43.glEnableVertexAttribArray(0)
        GL43.glEnableVertexAttribArray(1)
        GL43.glEnableVertexAttribArray(2)

        GL43.glDrawArrays(GL43.GL_POINTS, 0, numVertices)

        GL43.glDisableVertexAttribArray(0)
        GL43.glDisableVertexAttribArray(1)
        GL43.glDisableVertexAttribArray(2)
        GL43.glBindVertexArray(0)
    }

    private fun initializeEmptyVertexBuffer(): FloatBuffer {
        val vertexData = BufferUtils.createFloatBuffer(capacity * stride)
        for (i in 0..<vertexData.capacity()) {
            vertexData.put(0f)
        }
        vertexData.flip()

        return vertexData
    }
}