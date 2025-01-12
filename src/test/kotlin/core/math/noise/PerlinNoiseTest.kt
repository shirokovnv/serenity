package core.math.noise

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.math.abs

class PerlinNoiseTest {

    @Test
    fun `test noise values in range`() {
        val perlinNoise = PerlinNoise()
        for (i in 0..100) {
            val x = kotlin.random.Random.nextFloat() * 100
            val y = kotlin.random.Random.nextFloat() * 100
            val scale = kotlin.random.Random.nextFloat() * 0.5f
            val noiseValue = perlinNoise.noise(x, y, scale)

            assertFalse(noiseValue.isNaN())
            assertFalse(noiseValue.isInfinite())

            assertTrue(noiseValue in -1.0f..1.0f)
        }
    }
    @Test
    fun `test noise same values return same results`() {
        val perlinNoise = PerlinNoise()
        val x = 10.0f
        val y = 20.0f
        val scale = 0.5f

        val noise1 = perlinNoise.noise(x,y,scale)
        val noise2 = perlinNoise.noise(x,y,scale)
        assertTrue(abs(noise1 - noise2) < 0.0001f, "Same noise should produce similar results")
    }

    @Test
    fun `test zero coordinates`() {
        val perlinNoise = PerlinNoise()
        val zeroNoise = perlinNoise.noise(0f, 0f, 0.1f)
        assertFalse(zeroNoise.isNaN(), "Zero noise should not be NaN")
        assertFalse(zeroNoise.isInfinite(), "Zero noise should not be Infinite")
        assertTrue(zeroNoise in -1.0f..1.0f)
    }

    @Test
    fun `test int noise same as float noise`(){
        val perlinNoise = PerlinNoise()
        val x = 5
        val y = 8
        val scale = 0.2f
        val intNoise = perlinNoise.noise(x, y, scale);
        val floatNoise = perlinNoise.noise(x.toFloat(), y.toFloat(), scale);
        assertTrue(abs(intNoise - floatNoise) < 0.0001f);
    }
}