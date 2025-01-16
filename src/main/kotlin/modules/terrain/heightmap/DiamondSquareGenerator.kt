package modules.terrain.heightmap

import core.math.helpers.smoothStep
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
import kotlin.math.*
import kotlin.random.Random

class DiamondSquareGenerator() : HeightmapGenerationInterface<DiamondSquareParams> {
    override fun generate(width: Int, height: Int, params: DiamondSquareParams): FloatBuffer {
        val arrayOfHeights = buildHeightsArray(width, height, params.roughness)

        applyMask(width, height, arrayOfHeights, params.maskOffset)
        if (params.normalize) {
            normalizeHeights(arrayOfHeights)
        }

        return buildHeightsBuffer(width, height, arrayOfHeights)
    }

    private fun buildHeightsArray(width: Int, height: Int, roughness: Float): FloatArray {
        val arrayOfHeights = FloatArray(width * height) { 0f }

        // Fill the edges with random values
        arrayOfHeights[0] = Random.nextFloat()
        arrayOfHeights[width - 1] = Random.nextFloat()
        arrayOfHeights[height * (width - 1)] = Random.nextFloat()
        arrayOfHeights[width * height - 1] = Random.nextFloat()

        for (y in 0..<height) {
            for (x in 0..<width) {
                arrayOfHeights[y * width + x] = nextValue(x, y, width, height, arrayOfHeights, roughness)
            }
        }

        return arrayOfHeights
    }

    private fun applyMask(width: Int, height: Int, arrayOfHeights: FloatArray, maskOffset: Float) {
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(width, height) / 2f

        for (y in 0..<height) {
            for (x in 0..<width) {
                val distance = sqrt((x - centerX).pow(2) + (y - centerY).pow(2))

                var maskValue = 1.0f
                if (distance >= radius - maskOffset) {
                    val normalizedDistance = ((distance - (radius - maskOffset)) / maskOffset).coerceIn(0f, 1f)
                    maskValue = smoothStep(1f, 0f, normalizedDistance)
                }

                arrayOfHeights[y * width + x] *= maskValue
            }
        }
    }

    private fun normalizeHeights(arrayOfHeights: FloatArray) {
        var minVal = Float.MAX_VALUE
        var maxVal = Float.MIN_VALUE

        for (v in arrayOfHeights) {
            minVal = min(minVal, v)
            maxVal = max(maxVal, v)
        }

        for (i in arrayOfHeights.indices) {
            arrayOfHeights[i] = (arrayOfHeights[i] - minVal) / (maxVal - minVal)
        }
    }

    private fun buildHeightsBuffer(width: Int, height: Int, arrayOfHeights: FloatArray): FloatBuffer {
        val bufferOfHeights = BufferUtils.createFloatBuffer(width * height * 4)

        for (x in 0..<width) {
            for (y in 0..<height) {
                bufferOfHeights.put(arrayOfHeights[y * width + x])
                bufferOfHeights.put(arrayOfHeights[y * width + x])
                bufferOfHeights.put(arrayOfHeights[y * width + x])
                bufferOfHeights.put(1.0f)
            }
        }
        bufferOfHeights.flip()

        return bufferOfHeights
    }

    private fun nextValue(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        arrayOfHeights: FloatArray,
        roughness: Float,
        v: Float? = null
    ): Float {
        if (v != null) {
            arrayOfHeights[y * width + x] = max(0.0f, min(1.0f, v))
            return arrayOfHeights[y * width + x]
        } else {
            if (x < 0 || x >= width || y < 0 || y >= height) return 0.0f
            if (arrayOfHeights[y * width + x] == 0.0f) {
                var base = 1
                while (((x and base) == 0) && ((y and base) == 0)) {
                    base = base shl 1
                }
                if (((x and base) != 0) && ((y and base) != 0)) {
                    squareStep(x, y, base, width, height, arrayOfHeights, roughness)
                } else {
                    diamondStep(x, y, base, width, height, arrayOfHeights, roughness)
                }
            }
            return arrayOfHeights[y * width + x]
        }
    }

    private fun displace(v: Float, blockSize: Int, x: Int, y: Int, width: Int, height: Int, roughness: Float): Float {
        val randomValue = (Random.nextFloat() - 0.5f) * blockSize * 2f / min(width, height) * roughness
        return v + randomValue * (1 - (abs(x - (width / 2f)) / (width / 2f) + abs(y - (height / 2f)) / (height / 2f)) / 2f)
    }

    private fun squareStep(
        x: Int,
        y: Int,
        blockSize: Int,
        width: Int,
        height: Int,
        arrayOfHeights: FloatArray,
        roughness: Float
    ) {
        if (arrayOfHeights[y * width + x] == 0.0f) {
            val v = nextValue(
                x, y, width, height, arrayOfHeights, roughness,
                displace(
                    (nextValue(x - blockSize, y - blockSize, width, height, arrayOfHeights, roughness) +
                            nextValue(x + blockSize, y - blockSize, width, height, arrayOfHeights, roughness) +
                            nextValue(x - blockSize, y + blockSize, width, height, arrayOfHeights, roughness) +
                            nextValue(x + blockSize, y + blockSize, width, height, arrayOfHeights, roughness)) / 4f,
                    blockSize,
                    x,
                    y,
                    width,
                    height,
                    roughness
                )
            )

            arrayOfHeights[y * width + x] = v

        }
    }

    private fun diamondStep(
        x: Int,
        y: Int,
        blockSize: Int,
        width: Int,
        height: Int,
        arrayOfHeights: FloatArray,
        roughness: Float
    ) {
        if (arrayOfHeights[y * width + x] == 0.0f) {
            val v = nextValue(
                x, y, width, height, arrayOfHeights, roughness,
                displace(
                    (nextValue(x - blockSize, y, width, height, arrayOfHeights, roughness) +
                            nextValue(x + blockSize, y, width, height, arrayOfHeights, roughness) +
                            nextValue(x, y - blockSize, width, height, arrayOfHeights, roughness) +
                            nextValue(x, y + blockSize, width, height, arrayOfHeights, roughness)) / 4f,
                    blockSize,
                    x,
                    y,
                    width,
                    height,
                    roughness
                )
            )
            arrayOfHeights[y * width + x] = v
        }
    }
}