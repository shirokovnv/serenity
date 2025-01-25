package graphics.assets.buffer

import graphics.assets.Asset
import org.lwjgl.opengl.GL43.*

class Vao: Asset {
    private var vaoId: Int = 0

    init {
        create()
    }

    override fun getId(): Int {
        return vaoId
    }

    override fun create() {
        vaoId = glGenVertexArrays()
    }

    override fun destroy() {
        if (vaoId != 0) {
            glDeleteVertexArrays(vaoId)
            vaoId = 0
        }
    }

    override fun bind() {
        glBindVertexArray(vaoId)
    }

    override fun unbind() {
        glBindVertexArray(0)
    }
}