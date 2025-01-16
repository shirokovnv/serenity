package core.math.noise

import kotlin.test.Test
import kotlin.test.assertTrue

class PerlinOctaveNoiseTest {
    @Test
    fun `test octave noise basic`() {
        val noise = PerlinNoise()

        val noise1 = noise.octaveNoise(1f, 1f, 0.1f, 1, 1f, 0.5f)
        val noise2 = noise.octaveNoise(1f, 1f, 0.1f, 5, 1f, 0.5f)
        val noise3 = noise.octaveNoise(1f, 1f, 0.1f, 10, 1f, 0.5f)

        assertTrue(noise1 >= -1 && noise1 <= 1)
        assertTrue(noise2 >= -1 && noise2 <= 1)
        assertTrue(noise3 >= -1 && noise3 <= 1)

        assertTrue(noise1 != noise2 && noise2 != noise3)
    }

    @Test
    fun `test octave noise with different amplitude`() {
        val noise = PerlinNoise()

        val noise1 = noise.octaveNoise(1f, 1f, 0.1f, 3,  0.1f, 0.5f)
        val noise2 = noise.octaveNoise(1f, 1f, 0.1f, 3, 0.3f, 0.5f)
        val noise3 = noise.octaveNoise(1f, 1f, 0.1f, 3, 10f, 0.5f)

        assertTrue(noise1 != noise2 && noise2 != noise3)
    }

    @Test
    fun `test octave noise with different persistence`() {
        val noise = PerlinNoise()

        val noise1 = noise.octaveNoise(1f, 1f, 0.1f, 3, 1f, 0.1f)
        val noise2 = noise.octaveNoise(1f, 1f, 0.1f, 3, 1f, 1f)
        val noise3 = noise.octaveNoise(1f, 1f, 0.1f, 3, 1f, 10f)

        assertTrue(noise1 != noise2 && noise2 != noise3)
    }

    @Test
    fun `test octave noise with zero params`() {
        val noise = PerlinNoise()

        val noise1 = noise.octaveNoise(0f, 0f, 0.0f, 0, 0f, 0f)
        assertTrue(noise1 >= -1 && noise1 <= 1)

        val noise2 = noise.octaveNoise(1f, 1f, 0.0f, 0, 0f, 0f)
        assertTrue(noise2 >= -1 && noise2 <= 1)
    }

    @Test
    fun `test octave noise with big params`() {
        val noise = PerlinNoise()

        val noise1 = noise.octaveNoise(100f, 100f, 100f, 100, 0.9f, 1f)
        assertTrue(noise1 >= -1 && noise1 <= 1)
    }
}