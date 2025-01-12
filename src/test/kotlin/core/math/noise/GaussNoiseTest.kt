package core.math.noise

import org.junit.jupiter.api.Assertions.assertFalse
import kotlin.test.Test
import kotlin.math.abs
import kotlin.random.Random
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class GaussNoiseTest {
    @Test
    fun `test gauss random pair values`() {
        for (i in 0..100) {
            val mean = (Random.nextFloat() * 10f) - 5f
            val stdDeviation = Random.nextFloat() * 3f
            val noiseValue = GaussNoise().gaussRandomPair(mean, stdDeviation)
            assertFalse(noiseValue.x.isNaN())
            assertFalse(noiseValue.x.isInfinite())
            assertFalse(noiseValue.y.isNaN())
            assertFalse(noiseValue.y.isInfinite())
        }
    }

    @Test
    fun `test gauss random`() {
        for (i in 0..100) {
            val mean = (Random.nextFloat() * 10f) - 5f
            val stdDeviation = Random.nextFloat() * 3f
            val result = GaussNoise().gaussRandom(mean, stdDeviation)
            assertFalse(result.isNaN())
            assertFalse(result.isInfinite())
        }
    }

    @Test
    fun `test gauss random pair distribution`() {
        val mean = 0.0f
        val stdDeviation = 1.0f
        val samples = (0..1000).map {
            GaussNoise().gaussRandomPair(mean, stdDeviation)
        }.flatMap { listOf(it.x, it.y) }

        val sampleMean = samples.average()
        val sampleVariance = samples.sumOf { (it - sampleMean).pow(2) } / (samples.size - 1)
        val sampleStdDev = sqrt(sampleVariance)

        assertTrue(abs(sampleMean - mean) <= 0.1f)
        assertTrue(abs(sampleStdDev - stdDeviation) <= 0.1f)
    }

    @Test
    fun `test gauss random distribution`() {
        val mean = 0.0f
        val stdDeviation = 1.0f
        val samples = (0..1000).map {
            GaussNoise().gaussRandom(mean, stdDeviation)
        }

        val sampleMean = samples.average()
        val sampleVariance = samples.sumOf { (it - sampleMean).pow(2).toDouble() } / (samples.size - 1)
        val sampleStdDev = sqrt(sampleVariance)
        assertTrue(abs(sampleMean - mean) <= 0.1f)
        assertTrue(abs(sampleStdDev - stdDeviation) <= 0.1f)
    }

    @Test
    fun `test gauss random pair same seed`() {
        val random = Random(10)
        val mean = (random.nextFloat() * 10f) - 5f
        val stdDeviation = random.nextFloat() * 3f
        val result1 = GaussNoise().gaussRandomPair(mean, stdDeviation)
        val result2 = GaussNoise().gaussRandomPair(mean, stdDeviation)
        assertNotEquals(result1, result2)
    }
}