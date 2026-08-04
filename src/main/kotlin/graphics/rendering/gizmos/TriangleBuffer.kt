package graphics.rendering.gizmos

import core.math.Vector3
import graphics.assets.Asset
import graphics.assets.buffer.BufferUtil
import graphics.rendering.Drawable
import org.lwjgl.opengl.*

class TriangleBuffer(private val vertices: Array<Vector3>) : Asset, Drawable {

    private var vao = 0
    private var vbo = 0

    init {
        require(vertices.size == 3)
        create()
    }

    override fun getId(): Int = vao

    override fun create() {
        vao = GL30.glGenVertexArrays()
        GL43.glBindVertexArray(vao)

        vbo = GL43.glGenBuffers()
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vbo)

        GL43.glBufferData(
            GL43.GL_ARRAY_BUFFER,
            BufferUtil.createFlippedBuffer(vertices),
            GL43.GL_STATIC_DRAW
        )

        val stride = 3 * 4
        GL43.glVertexAttribPointer(
            0,
            3,
            GL43.GL_FLOAT,
            false,
            stride,
            0L
        )
        GL43.glEnableVertexAttribArray(0)

        GL43.glBindVertexArray(0)
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, 0)
    }

    override fun destroy() {
        if (vao != 0) {
            GL43.glDeleteVertexArrays(vao)
            vao = 0
        }
        if (vbo != 0) {
            GL43.glDeleteBuffers(vbo)
            vbo = 0
        }
    }

    override fun bind() {
        GL43.glBindVertexArray(vao)
    }

    override fun unbind() {
        GL43.glBindVertexArray(0)
    }

    override fun draw() {
        GL43.glDrawArrays(GL43.GL_TRIANGLES, 0, 3)
    }
}