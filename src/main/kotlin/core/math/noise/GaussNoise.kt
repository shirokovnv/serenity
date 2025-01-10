package core.math.noise

import core.math.Vector2
import kotlin.math.cos
import kotlin.math.sqrt
import kotlin.math.ln
import kotlin.math.sin
import kotlin.random.Random

class GaussNoise {
    fun gaussRandomPair(mean: Float, stdDeviation: Float): Vector2 {
        val u1 = Random.nextFloat()
        val u2 = Random.nextFloat()

        val z1 = sqrt(-2 * ln(u1)) * cos(2 * Math.PI.toFloat() * u2)
        val z2 = sqrt(-2 * ln(u1)) * sin(2 * Math.PI.toFloat() * u2)

        return Vector2(mean + z1 * stdDeviation, mean + z2 * stdDeviation)
    }

    fun gaussRandom(mean: Float, stdDeviation: Float): Float {
        val u1 = Random.nextFloat()
        val u2 = Random.nextFloat()

        val z1 = sqrt(-2 * ln(u1)) * cos(2 * Math.PI.toFloat() * u2)

        return mean + z1 * stdDeviation
    }
}