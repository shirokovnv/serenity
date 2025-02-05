package modules.terrain.heightmap

import core.math.Rect3d
import core.math.Vector2
import core.math.Vector3
import core.math.extensions.clamp
import core.scene.volumes.BoundsInterface
import core.scene.volumes.BoxAABB
import graphics.assets.texture.Texture2d
import graphics.assets.texture.TextureFactory
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import java.nio.FloatBuffer
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class Heightmap(
    heightMapTexture: Texture2d,
    worldScale: Vector3,
    worldOffset: Vector3
) : Basemap(heightMapTexture, worldScale, worldOffset), BoundsInterface {

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

    fun getHeightAt(x: Int, y: Int): Float {
        val tX = x.clamp(0, width - 1)
        val tY = y.clamp(0, height - 1)
        return mapData.get(tY * width + tX)
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

    fun getNormalAt(x: Int, y: Int, normalStrength: Float = 60.0f): Vector3 {
        val z0 = getHeightAt(x - 1, y - 1)
        val z1 = getHeightAt(x, y - 1)
        val z2 = getHeightAt(x + 1, y - 1)
        val z3 = getHeightAt(x - 1, y)
        val z4 = getHeightAt(x + 1, y)
        val z5 = getHeightAt(x - 1, y + 1)
        val z6 = getHeightAt(x, y + 1)
        val z7 = getHeightAt(x + 1, y + 1)

        val normal = Vector3()
        normal.z = 1.0f / normalStrength
        normal.x = z0 + 2 * z3 + z5 - z2 - 2 * z4 - z7
        normal.y = z0 + 2 * z1 + z2 - z5 - 2 * z6 - z7
        return normal.normalize()
    }

    fun getInterpolatedNormal(worldX: Float, worldY: Float, normalStrength: Float = 60.0f): Vector3 {
        val textureXY = worldToTexture(worldX, worldY)

        val x = textureXY.x.toInt()
        val y = textureXY.y.toInt()

        return getNormalAt(x, y, normalStrength)
    }

    override fun createMapDataBuffer(): FloatBuffer {
        val heightmapDataBuffer: FloatBuffer = BufferUtils.createFloatBuffer(width * height)
        mapTexture.bind()
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RED, GL11.GL_FLOAT, heightmapDataBuffer)
        return heightmapDataBuffer
    }

    override fun calculateBounds(): BoxAABB {
        val minX = worldOffset.x
        val minZ = worldOffset.z
        val maxX = worldOffset.x + worldScale.x
        val maxZ = worldOffset.z + worldScale.z
        var minY = Float.MAX_VALUE
        var maxY = Float.MIN_VALUE

        for (i in 0..<mapData.limit()) {
            val value = worldOffset.y + mapData.get(i) * worldScale.y
            minY = min(minY, value)
            maxY = max(maxY, value)
        }

        return BoxAABB(Rect3d(Vector3(minX, minY, minZ), Vector3(maxX, maxY, maxZ)))
    }

    fun calculatePatchBounds(
        minPoint: Vector2,
        maxPoint: Vector2,
    ): BoxAABB {
        val tMinPoint = worldToTexture(minPoint.x, minPoint.y)
        val tMaxPoint = worldToTexture(maxPoint.x, maxPoint.y)

        val minX = minPoint.x
        val maxX = maxPoint.x
        val minZ = minPoint.y
        val maxZ = maxPoint.y
        var minY = Float.MAX_VALUE
        var maxY = Float.MIN_VALUE

        for (y in tMinPoint.y.toInt()..<tMaxPoint.y.toInt()) {
            for (x in tMinPoint.x.toInt()..<tMaxPoint.x.toInt()) {
                val worldY = worldOffset.y + getHeightAt(x, y) * worldScale.y
                minY = min(minY, worldY)
                maxY = max(maxY, worldY)
            }
        }

        return BoxAABB(Rect3d(Vector3(minX, minY, minZ), Vector3(maxX, maxY, maxZ)))
    }
}