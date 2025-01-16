package core.math.noise

import org.junit.jupiter.api.Assertions.assertFalse
import kotlin.test.Test
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.assertTrue

class GaussNoiseTest {
    @Test
    fun `test gauss random`() {
        for (i in 0..100) {
            val result = GaussNoise().gaussRandom()
            assertFalse(result.isNaN())
            assertFalse(result.isInfinite())
        }
    }

    @Test
    fun `test gauss random distribution`() {
        val mean = 0.0f
        val stdDeviation = 1.0f
        val samples = (0..1000).map {
            GaussNoise().gaussRandom()
        }

        val sampleMean = samples.average()
        val sampleVariance = samples.sumOf { (it - sampleMean).pow(2) } / (samples.size - 1)
        val sampleStdDev = sqrt(sampleVariance)
        assertTrue(abs(sampleMean - mean) <= 0.1f)
        assertTrue(abs(sampleStdDev - stdDeviation) <= 0.1f)
    }
}