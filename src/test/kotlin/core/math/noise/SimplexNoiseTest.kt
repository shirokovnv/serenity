package core.math.noise

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SimplexNoiseTest {
    private val simplexNoise = SimplexNoise()

    @Test
    fun `test noise values in range`() {
        var x = -1.0f
        var y = -1.0f
        var z = -1.0f

        val octaves = Random.nextInt(1, 8)
        val stepSize = 0.1f

        while (x <= 1.0f) {
            val noise1d = simplexNoise.noise(x)
            val fractal1d = simplexNoise.fractal(octaves, x)
            assertTrue(noise1d >= -1.0f && noise1d <= 1.0f)
            assertTrue(fractal1d >= -1.0f && fractal1d <= 1.0f)

            while (y <= 1.0f) {
                val noise2d = simplexNoise.noise(x, y)
                val fractal2d = simplexNoise.fractal(octaves, x, y)
                assertTrue(noise2d >= -1.0f && noise2d <= 1.0f)
                assertTrue(fractal2d >= -1.0f && fractal2d <= 1.0f)

                while (z <= 1.0f) {
                    val noise3d = simplexNoise.noise(x, y, z)
                    val fractal3d = simplexNoise.fractal(octaves, x, y, z)
                    assertTrue(noise3d >= -1.0f && noise3d <= 1.0f)
                    assertTrue(fractal3d >= -1.0f && fractal3d <= 1.0f)

                    z += stepSize
                }
                y += stepSize
            }
            x += stepSize
        }
    }

    @Test
    fun `test noise repeatability`() {
        val x = Random.nextFloat()
        val y = Random.nextFloat()
        val z = Random.nextFloat()
        val octaves = Random.nextInt(1, 8)

        val firstNoise1d = simplexNoise.noise(x)
        val secondNoise1d = simplexNoise.noise(x)
        val firstFractal1d = simplexNoise.fractal(octaves, x)
        val secondFractal1d = simplexNoise.fractal(octaves, x)

        assertEquals(firstNoise1d, secondNoise1d)
        assertEquals(firstFractal1d, secondFractal1d)

        val firstNoise2d = simplexNoise.noise(x, y)
        val secondNoise2d = simplexNoise.noise(x, y)
        val firstFractal2d = simplexNoise.fractal(octaves, x, y)
        val secondFractal2d = simplexNoise.fractal(octaves, x, y)

        assertEquals(firstNoise2d, secondNoise2d)
        assertEquals(firstFractal2d, secondFractal2d)

        val firstNoise3d = simplexNoise.noise(x, y, z)
        val secondNoise3d = simplexNoise.noise(x, y, z)
        val firstFractal3d = simplexNoise.fractal(octaves, x, y, z)
        val secondFractal3d = simplexNoise.fractal(octaves, x, y, z)

        assertEquals(firstNoise3d, secondNoise3d)
        assertEquals(firstFractal3d, secondFractal3d)
    }
}