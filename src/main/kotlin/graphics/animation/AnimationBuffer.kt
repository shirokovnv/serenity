package graphics.animation

import graphics.assets.Asset
import graphics.rendering.Drawable
import org.lwjgl.opengl.GL43
import java.nio.FloatBuffer
import java.nio.IntBuffer

class AnimationBuffer(
    private val vertices: FloatBuffer,
    private val indices: IntBuffer
) : Asset, Drawable {

    companion object {
        const val VERTEX_SIZE = 19
        const val VERTEX_SIZE_WO_BONES = 11
        private const val STRIDE = VERTEX_SIZE * Float.SIZE_BYTES
    }

    private var vaoId: Int = 0
    private var vboId: Int = 0
    private var eboId: Int = 0

    private var numVertices: Int = 0

    init {
        create()
    }

    override fun getId(): Int {
        return vaoId
    }

    override fun create() {
        numVertices = indices.limit()

        vaoId = GL43.glGenVertexArrays()
        GL43.glBindVertexArray(vaoId)

        vboId = GL43.glGenBuffers()
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vboId)
        GL43.glBufferData(GL43.GL_ARRAY_BUFFER, vertices, GL43.GL_STATIC_DRAW)

        GL43.glVertexAttribPointer(0, 3, GL43.GL_FLOAT, false, STRIDE, 0)
        GL43.glEnableVertexAttribArray(0)

        GL43.glVertexAttribPointer(1, 2, GL43.GL_FLOAT, false, STRIDE, 12)
        GL43.glEnableVertexAttribArray(1)

        GL43.glVertexAttribPointer(2, 3, GL43.GL_FLOAT, false, STRIDE, 20)
        GL43.glEnableVertexAttribArray(2)

        GL43.glVertexAttribPointer(3, 4, GL43.GL_FLOAT, false, STRIDE, 32) // Bone Weights
        GL43.glEnableVertexAttribArray(3)

        GL43.glVertexAttribPointer(4, 4, GL43.GL_FLOAT, false, STRIDE, 44) // Bone Weights
        GL43.glEnableVertexAttribArray(4)

        GL43.glVertexAttribPointer(5, 4, GL43.GL_FLOAT, false, STRIDE, 60) // Bone Weights
        GL43.glEnableVertexAttribArray(5)

        eboId = GL43.glGenBuffers()
        GL43.glBindBuffer(GL43.GL_ELEMENT_ARRAY_BUFFER, eboId)
        GL43.glBufferData(GL43.GL_ELEMENT_ARRAY_BUFFER, indices, GL43.GL_STATIC_DRAW)

        GL43.glBindVertexArray(0)
    }

    override fun destroy() {
        if (vaoId != 0)
            GL43.glDeleteVertexArrays(vaoId)
        if (vboId != 0)
            GL43.glDeleteBuffers(vboId)
        if (eboId != 0)
            GL43.glDeleteBuffers(eboId)

        vaoId = 0
        vboId = 0
        eboId = 0
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
        GL43.glDrawElements(GL43.GL_TRIANGLES, numVertices, GL43.GL_UNSIGNED_INT, 0)
        GL43.glBindVertexArray(0)
    }
}