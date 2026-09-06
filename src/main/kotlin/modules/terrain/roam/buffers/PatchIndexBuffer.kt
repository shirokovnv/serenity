package modules.terrain.roam.buffers

import graphics.assets.buffer.BufferUtil
import modules.terrain.roam.tri.TriNodeGeometry
import org.lwjgl.opengl.GL43

class PatchIndexBuffer : PatchBufferInterface {
    companion object {
        private val instanceCapacity = TriNodeGeometry.maxLeafTriangles
    }

    private var vbo = 0
    private var vaoId = 0
    private var numInstances = 0

    init {
        create()
    }

    override fun getId(): Int {
        return vaoId
    }

    override fun create() {
        numInstances = instanceCapacity

        vbo = GL43.glGenBuffers()
        vaoId = GL43.glGenVertexArrays()

        GL43.glBindVertexArray(vaoId)
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vbo)
        GL43.glBufferData(
            GL43.GL_ARRAY_BUFFER,
            BufferUtil.createIntBuffer(instanceCapacity),
            GL43.GL_DYNAMIC_DRAW
        )
        GL43.glVertexAttribIPointer(0, 1, GL43.GL_INT, Int.SIZE_BYTES, 0)
        GL43.glVertexAttribDivisor(0, 1)

        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, 0)
        GL43.glBindVertexArray(0)
    }

    override fun destroy() {
        GL43.glBindVertexArray(vaoId)

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
        GL43.glDrawArraysInstanced(GL43.GL_TRIANGLES, 0, 3, numInstances)
    }

    fun uploadData(bufferData: IntArray) {
        if (bufferData.size > instanceCapacity) {
            throw RuntimeException("Increase instance buffer size!")
        }

        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vbo)
        GL43.glBufferSubData(GL43.GL_ARRAY_BUFFER, 0, bufferData)

        numInstances = bufferData.size
    }
}