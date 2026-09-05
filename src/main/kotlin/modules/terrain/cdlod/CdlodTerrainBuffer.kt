package modules.terrain.cdlod

import core.math.Vector2
import core.math.Vector3
import graphics.assets.Asset
import graphics.assets.buffer.BufferUtil
import graphics.rendering.Drawable
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL43
import org.lwjgl.opengl.GL45
import java.nio.FloatBuffer

class CdlodTerrainBuffer(
    private var gridSize: Int
) : Asset, Drawable {
    companion object {
        var instanceBufferScale: FloatBuffer? = null
        var instanceBufferLods: FloatBuffer? = null
        var instanceBufferLocs: FloatBuffer? = null
        var instanceBufferLowPoints: FloatBuffer? = null
        var instanceBufferHighPoints: FloatBuffer? = null

        private const val INSTANCE_BUFFER_SIZE = 50000 // It must be 4^QUADTREE_DEPTH
    }

    private var vbo = 0
    private var ibo = 0
    private var vaoId = 0

    private var scalingBuffer = 0
    private var lodBuffer = 0
    private var locBuffer = 0
    private var lowPointsBuffer = 0
    private var highPointsBuffer = 0

    private lateinit var locations: Array<Vector2>
    private lateinit var scales: Array<Vector3>
    private lateinit var lods: FloatArray
    private lateinit var lowPoints: Array<Vector3>
    private lateinit var highPoints: Array<Vector3>

    private var indexCount: Int = 0

    init {
        create()
    }

    fun setGridSize(gridSize: Int) {
        this.gridSize = gridSize
    }

    override fun getId(): Int {
        return vaoId
    }

    override fun create() {
        initializeInstanceBuffers()
        checkInstanceData()

        vbo = GL45.glGenBuffers()
        locBuffer = GL45.glGenBuffers()
        scalingBuffer = GL45.glGenBuffers()
        lodBuffer = GL45.glGenBuffers()
        lowPointsBuffer = GL45.glGenBuffers()
        highPointsBuffer = GL45.glGenBuffers()
        vaoId = GL45.glGenVertexArrays()

        val vertexSize = Float.SIZE_BYTES * 2

        GL43.glBindVertexArray(vaoId)

        // Vertex Data
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vbo)

        val vertices = generateVertices()
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
        instanceBufferLods = BufferUtil.createFlippedBuffer(*lods)
        GL45.glBufferData(GL45.GL_ARRAY_BUFFER, instanceBufferLods!!, GL45.GL_DYNAMIC_DRAW)

        GL45.glVertexAttribPointer(3, 1, GL45.GL_FLOAT, false, 0, 0)
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

        ibo = GL45.glGenBuffers()
        GL45.glBindBuffer(GL45.GL_ELEMENT_ARRAY_BUFFER, ibo)
        val indices = generateIndices()
        val indexBuffer = BufferUtil.createFlippedBuffer(*indices)
        GL45.glBufferData(GL45.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL45.GL_STATIC_DRAW)
        indexCount = indices.size

        GL45.glBindVertexArray(0)
    }

    override fun destroy() {
        GL43.glBindVertexArray(vaoId)
        GL43.glDeleteBuffers(vbo)
        GL43.glDeleteBuffers(ibo)
        GL43.glDeleteBuffers(scalingBuffer)
        GL43.glDeleteBuffers(lodBuffer)
        GL43.glDeleteBuffers(locBuffer)
        GL43.glDeleteBuffers(lowPointsBuffer)
        GL43.glDeleteBuffers(highPointsBuffer)
        GL43.glDeleteVertexArrays(vaoId)
        GL43.glBindVertexArray(0)

        disposeInstanceBuffers()
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
        GL45.glDrawElementsInstanced(GL45.GL_TRIANGLES, indexCount, GL45.GL_UNSIGNED_INT, 0, scales.size)
    }

    fun uploadData(
        locations: Array<Vector2>,
        scales: Array<Vector3>,
        lods: FloatArray,
        lowPoints: Array<Vector3>,
        highPoints: Array<Vector3>
    ) {
        checkInstanceData()

        this.scales = scales
        this.locations = locations
        this.lods = lods

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
                INSTANCE_BUFFER_SIZE
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
        for (lod in lods) {
            instanceBufferLods!!.put(lod)
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
        if (scales.size != locations.size || locations.size != lods.size) {
            throw RuntimeException("NEED PROPER NUMBER OF INSTANCE Vectors")
        }

        if (locations.size > INSTANCE_BUFFER_SIZE) {
            throw RuntimeException("Increase instance buffer size!")
        }
    }

    private fun generateVertices(): Array<Vector2> {
        val vertices = mutableListOf<Vector2>()

        for (y in 0..<gridSize) {
            for (x in 0..<gridSize) {
                // normalize UV [0, 1]
                val u = x.toFloat() / (gridSize - 1)
                val v = y.toFloat() / (gridSize - 1)
                vertices.add(Vector2(u, v))
            }
        }

        return vertices.toTypedArray()
    }

    private fun generateIndices(): IntArray {
        val indices = mutableListOf<Int>()

        for (y in 0..<gridSize - 1) {
            for (x in 0..<gridSize - 1) {

                val tl = y * gridSize + x          // top-left
                val tr = y * gridSize + (x + 1)    // top-right
                val bl = (y + 1) * gridSize + x    // bottom-left
                val br = (y + 1) * gridSize + (x + 1) // bottom-right

                // Triangle 1: TL -> BL -> TR (CCW)
                indices.add(tl)
                indices.add(bl)
                indices.add(tr)

                // Triangle 2: BL -> BR -> TR (CCW)
                indices.add(bl)
                indices.add(br)
                indices.add(tr)
            }
        }

        return indices.toIntArray()
    }

    private fun initializeInstanceBuffers() {
        scales = Array(INSTANCE_BUFFER_SIZE) { Vector3(0f, 0f, 0f) }
        locations = Array(INSTANCE_BUFFER_SIZE) { Vector2(0f, 0f) }
        lods = FloatArray(INSTANCE_BUFFER_SIZE) { 0f }
        lowPoints = Array(INSTANCE_BUFFER_SIZE) { Vector3(0f, 0f, 0f) }
        highPoints = Array(INSTANCE_BUFFER_SIZE) { Vector3(0f, 0f, 0f) }
    }

    private fun disposeInstanceBuffers() {
        instanceBufferLocs = null
        instanceBufferLods = null
        instanceBufferScale = null
        instanceBufferLowPoints = null
        instanceBufferHighPoints = null
    }
}