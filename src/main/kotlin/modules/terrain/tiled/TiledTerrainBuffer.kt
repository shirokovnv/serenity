package modules.terrain.tiled

import core.math.Vector2
import graphics.assets.Asset
import graphics.assets.buffer.BufferUtil
import graphics.rendering.Drawable
import org.lwjgl.opengl.*
import kotlin.properties.Delegates

class TiledTerrainBuffer(
    private val vertices: Array<Vector2>,
    private val offsets: Array<Vector2>
) : Asset, Drawable {
    private var vboId by Delegates.notNull<Int>()
    private var instanceBufferId by Delegates.notNull<Int>()
    private var vaoId by Delegates.notNull<Int>()

    private var numVertices by Delegates.notNull<Int>()
    private var numInstances by Delegates.notNull<Int>()

    init {
        create()
        instantiateBuffers()
    }

    override fun draw() {
        // INSTANCING
        GL30.glBindVertexArray(vaoId)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)

        GL42C.glDrawArraysInstancedBaseInstance(GL40.GL_PATCHES, 0, numVertices, numInstances, 0)

        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL30.glBindVertexArray(0)
    }

    override fun getId(): Int {
        return vaoId
    }

    override fun create() {
        vboId = GL15.glGenBuffers()
        instanceBufferId = GL15.glGenBuffers()
        vaoId = GL30.glGenVertexArrays()
    }

    override fun destroy() {
        GL30.glBindVertexArray(vaoId)
        GL15.glDeleteBuffers(vboId)
        GL15.glDeleteBuffers(instanceBufferId)
        GL30.glDeleteVertexArrays(vaoId)
        GL30.glBindVertexArray(0)
    }

    override fun bind() {
        GL30.glBindVertexArray(vaoId)
    }

    override fun unbind() {
        GL30.glBindVertexArray(0)
    }

    private fun instantiateBuffers() {
        numVertices = vertices.size
        numInstances = offsets.size

        GL30.glBindVertexArray(vaoId)

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, BufferUtil.createFlippedBuffer(vertices), GL15.GL_STATIC_DRAW)

        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, Float.SIZE_BYTES * 2, 0)
        GL40.glPatchParameteri(GL40.GL_PATCH_VERTICES, numVertices)

        // OFFSET VECTORS
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, instanceBufferId)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, BufferUtil.createFlippedBuffer(offsets), GL15.GL_STATIC_DRAW)

        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Float.SIZE_BYTES * 2, 0)
        GL33.glVertexAttribDivisor(1, 1)

        GL30.glBindVertexArray(0)
    }
}