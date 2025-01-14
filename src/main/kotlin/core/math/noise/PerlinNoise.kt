package core.math.noise

import core.math.Vector2
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.random.Random

class PerlinNoise {
    companion object {
        private const val TABLE_SIZE = 256
        private const val TABLE_MASK = TABLE_SIZE - 1
    }

    private val vecTable = Array(TABLE_SIZE) { Vector2() }
    private val lut = ByteArray(TABLE_SIZE)

    init {
        val step = 6.24f / TABLE_SIZE
        var value = 0.0f

        for (i in 0..<TABLE_SIZE) {
            vecTable[i].x = sin(value)
            vecTable[i].y = cos(value)
            value += step

            lut[i] = (Random.nextInt() and TABLE_MASK).toByte()
        }
    }

    private fun getVec(x: Int, y: Int): Vector2 {
        val a = lut[x and TABLE_MASK].toInt() and 0xFF
        val b = lut[y and TABLE_MASK].toInt() and 0xFF
        val value = lut[(a + b) and TABLE_MASK].toInt() and 0xFF
        return vecTable[value]
    }

    fun noise(x: Float, y: Float, scale: Float): Float {
        val pos = Vector2(x * scale, y * scale)

        val x0 = floor(pos.x)
        val x1 = x0 + 1.0f
        val y0 = floor(pos.y)
        val y1 = y0 + 1.0f

        val v0 = getVec(x0.toInt(), y0.toInt())
        val v1 = getVec(x0.toInt(), y1.toInt())
        val v2 = getVec(x1.toInt(), y0.toInt())
        val v3 = getVec(x1.toInt(), y1.toInt())

        val d0 = Vector2(pos.x - x0, pos.y - y0)
        val d1 = Vector2(pos.x - x0, pos.y - y1)
        val d2 = Vector2(pos.x - x1, pos.y - y0)
        val d3 = Vector2(pos.x - x1, pos.y - y1)

        val h0 = d0.x * v0.x + d0.y * v0.y
        val h1 = d1.x * v1.x + d1.y * v1.y
        val h2 = d2.x * v2.x + d2.y * v2.y
        val h3 = d3.x * v3.x + d3.y * v3.y

        val sx = 6 * (d0.x).pow(5) - 15 * (d0.x).pow(4) + 10 * (d0.x).pow(3)
        val sy = 6 * (d0.y).pow(5) - 15 * (d0.y).pow(4) + 10 * (d0.y).pow(3)

        val avgX0 = h0 + sx * (h2 - h0)
        val avgX1 = h1 + sx * (h3 - h1)
        return avgX0 + sy * (avgX1 - avgX0)
    }

    // TODO: fix some issues with integer scales, for ex x = 1, y = 1, scale = 1 always returns 0
    fun noise(x: Int, y: Int, scale: Float): Float {
        return noise(x.toFloat(), y.toFloat(), scale)
    }

    fun octaveNoise(x: Float, y: Float, scale: Float, octaves: Int, amplitude: Float, persistence: Float): Float {
        var total = 0.0f
        var maxValue = 0.0f
        var currentAmplitude = amplitude

        for (i in 0..<octaves) {
            total += noise(x, y, scale) * currentAmplitude

            maxValue += amplitude
            currentAmplitude *= persistence
        }

        return total / (maxValue + 0.0001f)
    }
}