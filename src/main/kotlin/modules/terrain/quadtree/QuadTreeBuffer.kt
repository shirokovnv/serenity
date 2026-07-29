package modules.terrain.quadtree

import core.math.*
import graphics.assets.Asset
import graphics.assets.buffer.BufferUtil
import graphics.rendering.Drawable
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*
import java.nio.FloatBuffer

class QuadTreeBuffer(
    private var vertices: Array<Vector2>
): Asset, Drawable {

    enum class PrimitiveType {
        PATCH,
        TRIANGLE
    }

    companion object {
        var instanceBufferScale: FloatBuffer? = null
        var instanceBufferLods: FloatBuffer? = null
        var instanceBufferLocs: FloatBuffer? = null
        var instanceBufferLowPoints: FloatBuffer? = null
        var instanceBufferHighPoints: FloatBuffer? = null

        private const val INSTANCE_BUFFER_SIZE = 50000 // It must be 4^QUADTREE_DEPTH
    }

    private var vbo = 0
    private var vaoId = 0

    private var scalingBuffer = 0
    private var lodBuffer = 0
    private var locBuffer = 0
    private var lowPointsBuffer = 0
    private var highPointsBuffer = 0

    var primitiveType: PrimitiveType = PrimitiveType.PATCH

    private var locations: Array<Vector2>
    private var scales: Array<Vector3>
    private var lodVectors: Array<Quaternion>
    private var lowPoints: Array<Vector3>
    private var highPoints: Array<Vector3>

    init {
        scales = Array(INSTANCE_BUFFER_SIZE) { Vector3(0f, 0f, 0f) }
        locations = Array(INSTANCE_BUFFER_SIZE) { Vector2(0f, 0f) }
        lodVectors = Array(INSTANCE_BUFFER_SIZE) { Quaternion(0f, 0f, 0f, 0f) }
        lowPoints = Array(INSTANCE_BUFFER_SIZE) { Vector3(0f, 0f, 0f) }
        highPoints = Array(INSTANCE_BUFFER_SIZE) { Vector3(0f, 0f, 0f) }

        create()
    }

    override fun getId(): Int {
        return vaoId
    }

    override fun create() {
        checkInstanceData()

        vbo = GL45.glGenBuffers()
        locBuffer = GL45.glGenBuffers()
        scalingBuffer = GL45.glGenBuffers()
        lodBuffer = GL45.glGenBuffers()
        lowPointsBuffer = GL45.glGenBuffers()
        highPointsBuffer = GL45.glGenBuffers()
        vaoId = GL45.glGenVertexArrays()

        val vertexSize = Float.SIZE_BYTES * 2
        val locationSize = Float.SIZE_BYTES * 2
        val scaleSize = Float.SIZE_BYTES * 3
        val lodVectorSize = Float.SIZE_BYTES * 4

        GL43.glBindVertexArray(vaoId)

        // Vertex Data
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vbo)

        val vertexBuffer = BufferUtil.createFlippedBuffer(vertices)
        GL45.glBufferData(GL45.GL_ARRAY_BUFFER, vertexBuffer, GL45.GL_STATIC_DRAW)

        GL45.glVertexAttribPointer(0, 2, GL45.GL_FLOAT, false, vertexSize, 0)
        GL45.glVertexAttribDivisor(0, 0)

        // LOCATION VECTORS
        GL45.glBindBuffer(GL45.GL_ARRAY_BUFFER, locBuffer)
        instanceBufferLocs = BufferUtil.createFlippedBuffer(locations)
        GL45.glBufferData(GL45.GL_ARRAY_BUFFER, instanceBufferLocs!!, GL45.GL_DYNAMIC_DRAW)

        GL45.glVertexAttribPointer(1, 2, GL45.GL_FLOAT, false, 0, 0)
        GL45.glVertexAttribDivisor(1, 1)

        // SCALE VECTORS
        GL45.glBindBuffer(GL45.GL_ARRAY_BUFFER, scalingBuffer)
        instanceBufferScale = BufferUtil.createFlippedBuffer(scales)
        GL45.glBufferData(GL45.GL_ARRAY_BUFFER, instanceBufferScale!!, GL45.GL_DYNAMIC_DRAW)

        GL45.glVertexAttribPointer(2, 3, GL45.GL_FLOAT, false, 0, 0)
        GL45.glVertexAttribDivisor(2, 1)

        // LOD VECTORS
        GL45.glBindBuffer(GL45.GL_ARRAY_BUFFER, lodBuffer)
        instanceBufferLods = BufferUtil.createFlippedBuffer(lodVectors)
        GL45.glBufferData(GL45.GL_ARRAY_BUFFER, instanceBufferLods!!, GL45.GL_DYNAMIC_DRAW)

        GL45.glVertexAttribPointer(3, 4, GL45.GL_FLOAT, false, 0, 0)
        GL45.glVertexAttribDivisor(3, 1)

        // LOW POINTS
        GL45.glBindBuffer(GL45.GL_ARRAY_BUFFER, lowPointsBuffer)
        instanceBufferLowPoints = BufferUtil.createFlippedBuffer(lowPoints)
        GL45.glBufferData(GL45.GL_ARRAY_BUFFER, instanceBufferLowPoints!!, GL45.GL_DYNAMIC_DRAW)

        GL45.glVertexAttribPointer(4, 3, GL45.GL_FLOAT, false, 0, 0)
        GL45.glVertexAttribDivisor(4, 1)

        // HIGH POINTS
        GL45.glBindBuffer(GL45.GL_ARRAY_BUFFER, highPointsBuffer)
        instanceBufferHighPoints = BufferUtil.createFlippedBuffer(highPoints)
        GL45.glBufferData(GL45.GL_ARRAY_BUFFER, instanceBufferHighPoints!!, GL45.GL_DYNAMIC_DRAW)

        GL45.glVertexAttribPointer(5, 3, GL45.GL_FLOAT, false, 0, 0)
        GL45.glVertexAttribDivisor(5, 1)

        GL45.glBindVertexArray(0)
    }

    override fun destroy() {
        GL43.glBindVertexArray(vaoId)
        GL43.glDeleteBuffers(vbo)
        GL43.glDeleteBuffers(scalingBuffer)
        GL43.glDeleteBuffers(lodBuffer)
        GL43.glDeleteBuffers(locBuffer)
        GL43.glDeleteBuffers(lowPointsBuffer)
        GL43.glDeleteBuffers(highPointsBuffer)
        GL43.glDeleteVertexArrays(vaoId)
        GL43.glBindVertexArray(0)
    }

    override fun bind() {
        GL45.glBindVertexArray(vaoId)
        GL45.glEnableVertexAttribArray(0)
        GL45.glEnableVertexAttribArray(1)
        GL45.glEnableVertexAttribArray(2)
        GL45.glEnableVertexAttribArray(3)
        GL45.glEnableVertexAttribArray(4)
        GL45.glEnableVertexAttribArray(5)
    }

    override fun unbind() {
        GL45.glDisableVertexAttribArray(0)
        GL45.glDisableVertexAttribArray(1)
        GL45.glDisableVertexAttribArray(2)
        GL45.glDisableVertexAttribArray(3)
        GL45.glDisableVertexAttribArray(4)
        GL45.glDisableVertexAttribArray(5)
        GL45.glBindVertexArray(0)
    }

    override fun draw() {
        val glPrimitiveType = when (primitiveType) {
            PrimitiveType.PATCH -> GL45.GL_PATCHES
            PrimitiveType.TRIANGLE -> GL45.GL_TRIANGLES
        }

        GL45.glPatchParameteri(GL45.GL_PATCH_VERTICES, vertices.size)
        GL45.glDrawArraysInstancedBaseInstance(glPrimitiveType, 0, vertices.size, scales.size, 0)
    }

    fun uploadData(
        locations: Array<Vector2>,
        scales: Array<Vector3>,
        lodVectors: Array<Quaternion>,
        lowPoints: Array<Vector3>,
        highPoints: Array<Vector3>
    ) {
        checkInstanceData()

        this.scales = scales
        this.locations = locations
        this.lodVectors = lodVectors

        if (instanceBufferLocs == null) {
            instanceBufferLocs = BufferUtils.createFloatBuffer(
                INSTANCE_BUFFER_SIZE * Float.SIZE_BYTES * 2
            )
        }
        if (instanceBufferScale == null) {
            instanceBufferScale = BufferUtils.createFloatBuffer(
                INSTANCE_BUFFER_SIZE * Float.SIZE_BYTES * 3
            )
        }
        if (instanceBufferLods == null) {
            instanceBufferLods = BufferUtils.createFloatBuffer(
                INSTANCE_BUFFER_SIZE * Float.SIZE_BYTES * 4
            )
        }
        if (instanceBufferLowPoints == null) {
            instanceBufferLowPoints = BufferUtils.createFloatBuffer(
                INSTANCE_BUFFER_SIZE * Float.SIZE_BYTES * 3
            )
        }
        if (instanceBufferHighPoints == null) {
            instanceBufferHighPoints = BufferUtils.createFloatBuffer(
                INSTANCE_BUFFER_SIZE * Float.SIZE_BYTES * 3
            )
        }

        // SET LOCATION VECTORS
        instanceBufferLocs!!.rewind().clear()
        for (loc in locations) {
            instanceBufferLocs!!.put(loc.x)
            instanceBufferLocs!!.put(loc.y)
        }
        instanceBufferLocs!!.flip()

        GL45.glBindBuffer(GL45.GL_ARRAY_BUFFER, locBuffer)
        GL45.glBufferSubData(GL45.GL_ARRAY_BUFFER, 0, instanceBufferLocs!!)

        // SET SCALE VECTORS
        instanceBufferScale!!.rewind().clear()
        for (scale in scales) {
            instanceBufferScale!!.put(scale.x)
            instanceBufferScale!!.put(scale.y)
            instanceBufferScale!!.put(scale.z)
        }
        instanceBufferScale!!.flip()

        GL45.glBindBuffer(GL45.GL_ARRAY_BUFFER, scalingBuffer)
        GL45.glBufferSubData(GL45.GL_ARRAY_BUFFER, 0, instanceBufferScale!!)

        // SET LOD VECTORS
        instanceBufferLods!!.rewind().clear()
        for (lod in lodVectors) {
            instanceBufferLods!!.put(lod.x)
            instanceBufferLods!!.put(lod.y)
            instanceBufferLods!!.put(lod.z)
            instanceBufferLods!!.put(lod.w)
        }
        instanceBufferLods!!.flip()

        GL45.glBindBuffer(GL45.GL_ARRAY_BUFFER, lodBuffer)
        GL45.glBufferSubData(GL45.GL_ARRAY_BUFFER, 0, instanceBufferLods!!)

        // SET LOW POINTS
        instanceBufferLowPoints!!.rewind().clear()
        for (low in lowPoints) {
            instanceBufferLowPoints!!.put(low.x)
            instanceBufferLowPoints!!.put(low.y)
            instanceBufferLowPoints!!.put(low.z)
        }
        instanceBufferLowPoints!!.flip()

        GL45.glBindBuffer(GL45.GL_ARRAY_BUFFER, lowPointsBuffer)
        GL45.glBufferSubData(GL45.GL_ARRAY_BUFFER, 0, instanceBufferLowPoints!!)

        // SET HIGH POINTS
        instanceBufferHighPoints!!.rewind().clear()
        for (high in highPoints) {
            instanceBufferHighPoints!!.put(high.x)
            instanceBufferHighPoints!!.put(high.y)
            instanceBufferHighPoints!!.put(high.z)
        }
        instanceBufferHighPoints!!.flip()

        GL45.glBindBuffer(GL45.GL_ARRAY_BUFFER, highPointsBuffer)
        GL45.glBufferSubData(GL45.GL_ARRAY_BUFFER, 0, instanceBufferHighPoints!!)
    }

    private fun checkInstanceData() {
        if (scales.size != locations.size || locations.size != lodVectors.size) {
            throw RuntimeException("NEED PROPER NUMBER OF INSTANCE Vectors")
        }

        if (locations.size > INSTANCE_BUFFER_SIZE) {
            throw RuntimeException("Increase instance buffer size!")
        }
    }
}