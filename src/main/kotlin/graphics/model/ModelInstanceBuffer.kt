package graphics.model

import core.math.Matrix4
import graphics.assets.Asset
import graphics.assets.buffer.BufferUtil
import org.lwjgl.opengl.GL43.*

class ModelInstanceBuffer(
    private val matrices: Array<Matrix4>,
    private val vaoIds: Array<Int>,
    private val attributeIndexOffset: Int = 3
) : Asset {
    private var instanceBufferId: Int = 0

    init {
        check(vaoIds.isNotEmpty())
        create()
    }

    override fun getId(): Int {
        return instanceBufferId
    }

    override fun create() {
        instanceBufferId = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, instanceBufferId)
        glBufferData(GL_ARRAY_BUFFER, BufferUtil.createFlippedBuffer(matrices), GL_STATIC_DRAW)

        vaoIds.forEach { vaoId ->
            glBindVertexArray(vaoId)

            var pointer: Long = 0
            val mat4Size = Float.SIZE_BYTES * 16
            for (i in 0..3) {
                glEnableVertexAttribArray(attributeIndexOffset + i)
                glVertexAttribPointer(attributeIndexOffset + i, 4, GL_FLOAT, false, mat4Size, pointer)
                glVertexAttribDivisor(attributeIndexOffset + i, 1)
                pointer += Float.SIZE_BYTES * 4L
            }

            glBindVertexArray(0)
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    override fun destroy() {
        glDeleteBuffers(instanceBufferId)
    }

    override fun bind() {
        vaoIds.forEach { vaoId ->
            bindForVaoId(vaoId)
        }
    }

    override fun unbind() {
        vaoIds.forEach { vaoId ->
            unbindForVaoId(vaoId)
        }
        glBindVertexArray(0)
    }

    fun bindForVaoId(vaoId: Int) {
        glBindVertexArray(vaoId)
        for (i in 0..3) {
            glEnableVertexAttribArray(attributeIndexOffset + i)
        }
    }

    fun unbindForVaoId(vaoId: Int) {
        glBindVertexArray(vaoId)
        for (i in 0..3) {
            glDisableVertexAttribArray(attributeIndexOffset + i)
        }
    }
}