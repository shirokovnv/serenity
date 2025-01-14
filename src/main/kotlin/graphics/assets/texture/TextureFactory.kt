package graphics.assets.texture

import core.math.noise.*
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL40C
import java.nio.FloatBuffer
import kotlin.math.tanh

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

    fun fromGaussNoise(width: Int, height: Int, mean: Float, deviation: Float): Texture2d {
        val gaussNoise = GaussNoise()
        val params = GaussNoiseParams(mean, deviation)
        return buildTextureWithNoiseCallback(width, height, gaussNoise, params, ::gaussNoiseCallback)
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

        for (y in 0..<height) {
            for (x in 0..<width) {
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
        val noise = noiseInstance.gaussRandom(params.mean, params.deviation)
        buffer.put(0.5f * tanh(noise) + 0.5f)
    }
}