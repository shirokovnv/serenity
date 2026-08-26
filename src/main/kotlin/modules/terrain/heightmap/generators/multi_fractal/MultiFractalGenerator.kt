package modules.terrain.heightmap.generators.multi_fractal

import core.math.extensions.clamp
import modules.terrain.heightmap.filters.HeightFilterInterface
import modules.terrain.heightmap.filters.HeightmapFilterInterface
import modules.terrain.heightmap.generators.HeightmapGenerationInterface
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
import kotlin.math.min

class MultiFractalGenerator : HeightmapGenerationInterface<MultiFractalParams> {
    override fun generate(width: Int, height: Int, params: MultiFractalParams): FloatBuffer {
        val heightmap = FloatArray(width * height)

        for (x in 0..<width) {
            for (y in 0..<height) {
                val wx = x.toFloat()
                val wy = y.toFloat()

                var value = params.noise.octaveNoise(
                    wx,
                    wy,
                    params.noiseParams.scale,
                    params.noiseParams.octave,
                    params.noiseParams.amplitude,
                    params.noiseParams.persistence
                )

                params.filters.filterIsInstance<HeightFilterInterface>().forEach { instance ->
                    value = instance.filter(x, y, value)
                }

                if (params.normalize) {
                    value = value * 0.5f + 0.5f
                    value = value.clamp(0f, 1f)
                }

                heightmap[x * width + y] = value
            }
        }

        params.filters.filterIsInstance<HeightmapFilterInterface>().forEach { instance ->
            instance.filter(heightmap, min(width, height))
        }

        val bufferOfHeights = BufferUtils.createFloatBuffer(width * height * 4)
        for (x in 0..<width) {
            for (y in 0..<height) {
                val value = heightmap[x * width + y]

                bufferOfHeights.put(value)
                bufferOfHeights.put(value)
                bufferOfHeights.put(value)
                bufferOfHeights.put(1.0f)
            }
        }
        bufferOfHeights.flip()

        return bufferOfHeights
    }
}