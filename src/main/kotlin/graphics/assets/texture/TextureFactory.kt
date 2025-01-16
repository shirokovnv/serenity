package graphics.assets.texture

import core.math.noise.*
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL40C
import java.nio.FloatBuffer

typealias TextureNoiseCallback = (
    x: Float,
    y: Float,
    buffer:
    FloatBuffer,
    noiseInstance: NoiseInterface,
    params: NoiseParams
) -> Unit

object TextureFactory {
    fun fromPerlinNoise(
        width: Int,
        height: Int,
        scale: Float,
        octaves: Int = 3,
        amplitude: Float = 1.0f,
        persistence: Float = 0.2f
    ): Texture2d {
        val perlinNoise = PerlinNoise()
        val params = OctaveNoiseParams(scale, octaves, amplitude, persistence)
        return buildTextureWithNoiseCallback(width, height, perlinNoise, params, ::octaveNoiseCallback)
    }

    fun fromGaussNoise(width: Int, height: Int, scale: Float = 1.0f): Texture2d {
        val gaussNoise = GaussNoise()
        val params = GaussNoiseParams(scale)
        return buildTextureWithNoiseCallback(width, height, gaussNoise, params, ::gaussNoiseCallback)
    }

    fun fromBuffer(textureBuffer: FloatBuffer, width: Int, height: Int): Texture2d {

        if (textureBuffer.capacity() != width * height * 4) {
            throw IllegalStateException("Wrong texture buffer capacity.")
        }

        val texture2d = Texture2d(width, height)
        texture2d.bind()
        texture2d.bilinearFilter()

        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D,
            0,
            GL40C.GL_RGBA32F,
            width,
            height,
            0,
            GL11.GL_RGBA,
            GL11.GL_FLOAT,
            textureBuffer
        )
        texture2d.unbind()

        return texture2d
    }

    private fun buildTextureWithNoiseCallback(
        width: Int,
        height: Int,
        noiseInstance: NoiseInterface,
        params: NoiseParams,
        callback: TextureNoiseCallback
    ): Texture2d {
        val texture2d = Texture2d(width, height)
        val buffer = BufferUtils.createFloatBuffer(width * height * 4)

        for (x in 0..<width) {
            for (y in 0..<height) {
                callback(x.toFloat(), y.toFloat(), buffer, noiseInstance, params)
            }
        }
        buffer.flip()

        texture2d.bind()
        texture2d.bilinearFilter()
        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D,
            0,
            GL40C.GL_RGBA32F,
            width,
            height,
            0,
            GL11.GL_RGBA,
            GL11.GL_FLOAT,
            buffer
        )
        texture2d.unbind()

        return texture2d
    }
}

fun octaveNoiseCallback(x: Float, y: Float, buffer: FloatBuffer, noiseInstance: NoiseInterface, params: NoiseParams) {
    if (noiseInstance !is PerlinNoise || params !is OctaveNoiseParams) {
        return
    }

    val noise = 0.5f * noiseInstance.octaveNoise(
        x,
        y,
        params.scale,
        params.octave,
        params.amplitude,
        params.persistence
    ) + 0.5f

    buffer.put(noise)
    buffer.put(noise)
    buffer.put(noise)
    buffer.put(1.0f)
}

fun gaussNoiseCallback(x: Float, y: Float, buffer: FloatBuffer, noiseInstance: NoiseInterface, params: NoiseParams) {
    if (noiseInstance !is GaussNoise || params !is GaussNoiseParams) {
        return
    }

    for (i in 0..<3) {
        val noise = noiseInstance.gaussRandom()
        buffer.put(noise / params.scale)
    }
}