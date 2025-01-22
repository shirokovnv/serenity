package modules.terrain.heightmap

import core.math.Quaternion
import core.math.Vector3
import graphics.assets.texture.Texture2d
import graphics.assets.texture.TextureChannel
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import java.nio.FloatBuffer

class Blendmap(
    blendMapTexture: Texture2d,
    worldScale: Vector3,
    worldOffset: Vector3
): Basemap(blendMapTexture, worldScale, worldOffset){

    fun readRGBA(x: Int, y: Int): Quaternion {
        val pixelIndex = (y * width + x) * 4
        if (pixelIndex < 0 || pixelIndex >= width * height * 4) {
            return Quaternion(0f)
        }

        return Quaternion(
            mapData[pixelIndex],
            mapData[pixelIndex + 1],
            mapData[pixelIndex + 2],
            mapData[pixelIndex + 3],
        )
    }

    fun readRGBA(worldX: Float, worldY: Float): Quaternion {
        val textureXY = worldToTexture(worldX, worldY)
        return readRGBA(textureXY.x.toInt(), textureXY.y.toInt())
    }

    fun readChannel(x: Int, y: Int, channel: TextureChannel): Float {
        val pixelIndex = (y * width + x) * 4
        if (pixelIndex < 0 || pixelIndex >= width * height * 4) {
            return 0.0f
        }

        return when(channel) {
            TextureChannel.R -> mapData[pixelIndex]
            TextureChannel.G -> mapData[pixelIndex + 1]
            TextureChannel.B -> mapData[pixelIndex + 2]
            TextureChannel.A -> mapData[pixelIndex + 3]
        }
    }

    fun readChannel(worldX: Float, worldY: Float, channel: TextureChannel): Float {
        val textureXY = worldToTexture(worldX, worldY)
        return readChannel(textureXY.x.toInt(), textureXY.y.toInt(), channel)
    }

    override fun createMapDataBuffer(): FloatBuffer {
        val blendMapDataBuffer: FloatBuffer = BufferUtils.createFloatBuffer(width * height * 4)
        mapTexture.bind()
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_FLOAT, blendMapDataBuffer)
        return blendMapDataBuffer
    }
}