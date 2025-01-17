package modules.terrain.heightmap

import core.math.Vector2
import core.math.Vector3
import core.math.extensions.clamp
import graphics.assets.texture.Texture2d
import graphics.assets.texture.TextureFactory
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import java.nio.FloatBuffer
import kotlin.math.floor
import kotlin.math.min

class Heightmap(
    private var heightMapTexture: Texture2d,
    private val worldScale: Vector3,
    private val worldOffset: Vector3
) {

    companion object {
        fun <TGenerator : HeightmapGenerationInterface<TParams>, TParams : HeightmapGenerationParams> fromGenerator(
            generator: TGenerator,
            params: TParams,
            width: Int,
            height: Int,
            worldOffset: Vector3,
            worldScale: Vector3
        ): Heightmap {

            val heightMapBuffer = generator.generate(width, height, params)
            val heightTexture = TextureFactory.fromBuffer(heightMapBuffer, width, height)
            return Heightmap(heightTexture, worldScale, worldOffset)
        }
    }

    private lateinit var heightmapData: FloatBuffer
    private val width
        get() = heightMapTexture.getWidth()
    private val height
        get() = heightMapTexture.getHeight()

    init {
        heightmapData = createHeightMapDataBuffer()
    }

    fun getHeightAt(x: Int, y: Int): Float {
        val tX = x.clamp(0, width - 1)
        val tY = y.clamp(0, height - 1)
        return heightmapData.get(tY * width + tX)
    }

    fun getInterpolatedHeight(worldX: Float, worldY: Float): Float {
        val textureXY = worldToTexture(worldX, worldY)

        val x0 = floor(textureXY.x).toInt()
        val y0 = floor(textureXY.y).toInt()
        val x1 = min(x0 + 1, width - 1)
        val y1 = min(y0 + 1, height - 1)

        val sx = textureXY.x - x0
        val sy = textureXY.y - y0

        val h00 = getHeightAt(x0, y0)
        val h10 = getHeightAt(x1, y0)
        val h01 = getHeightAt(x0, y1)
        val h11 = getHeightAt(x1, y1)

        val ix0 = h00 + sx * (h10 - h00)
        val ix1 = h01 + sx * (h11 - h01)

        return ix0 + sy * (ix1 - ix0)
    }

    fun getInterpolatedNormal(worldX: Float, worldY: Float, normalStrength: Float = 60.0f): Vector3 {
        val textureXY = worldToTexture(worldX, worldY)

        val x = textureXY.x.toInt()
        val y = textureXY.y.toInt()

        val z0 = getHeightAt(x - 1, y - 1)
        val z1 = getHeightAt(x , y - 1)
        val z2 = getHeightAt(x + 1, y - 1)
        val z3 = getHeightAt(x - 1, y )
        val z4 = getHeightAt(x + 1, y )
        val z5 = getHeightAt(x - 1, y + 1)
        val z6 = getHeightAt(x , y + 1)
        val z7 = getHeightAt(x + 1, y + 1)

        val normal = Vector3()
        normal.z = 1.0f / normalStrength
        normal.x = z0 + 2 * z3 + z5 - z2 - 2 * z4 - z7
        normal.y = z0 + 2 * z1 + z2 - z5 - 2 * z6 - z7
        return normal.normalize()
    }

    fun getTexture(): Texture2d = heightMapTexture

    fun getWorldScale(): Vector3 = worldScale

    fun getWorldOffset(): Vector3 = worldOffset

    private fun createHeightMapDataBuffer(): FloatBuffer {
        val heightmapDataBuffer: FloatBuffer = BufferUtils.createFloatBuffer(width * height)
        heightMapTexture.bind()
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RED, GL11.GL_FLOAT, heightmapDataBuffer)
        return heightmapDataBuffer
    }

    private fun worldToTexture(worldX: Float, worldY: Float): Vector2 {
        val scaledX = (worldX - worldOffset.x) / worldScale.x
        val scaledY = (worldY - worldOffset.z) / worldScale.z

        val textureX = (scaledX * width).coerceIn(0f, (width - 1).toFloat())
        val textureY = (scaledY * height).coerceIn(0f, (height - 1).toFloat())

        return Vector2(textureX, textureY)
    }
}