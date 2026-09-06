package modules.terrain.roam.buffers

import core.math.Vector2
import graphics.assets.buffer.BufferUtil
import graphics.assets.buffer.Ssbo
import org.lwjgl.opengl.GL43

class PatchSsbo(private val vertices: Array<Vector2>) : Ssbo() {
    private var ssboId: Int = 0

    init {
        create()
    }

    override fun getId(): Int {
        return ssboId
    }

    override fun create() {
        ssboId = GL43.glGenBuffers()
        GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, ssboId)

        val floatBuffer = BufferUtil.createFlippedBuffer(vertices)
        GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, floatBuffer, GL43.GL_STATIC_READ)

        GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0)
    }

    override fun destroy() {
        if (ssboId != 0) {
            GL43.glDeleteBuffers(ssboId)
            ssboId = 0
        }
    }

    override fun bind() {
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPoint, ssboId)
    }

    override fun unbind() {
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, bindingPoint, 0)
    }
}