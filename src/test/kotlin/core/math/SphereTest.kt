package core.math

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class SphereTest {

    @Test
    fun `test default sphere`() {
        val center = Vector3(1f, 2f, 3f)
        val sphere = Sphere(center, 5f)
        assertEquals(center, sphere.center)
    }

    @Test
    fun `test sphere with positive radius`() {
        val sphere = Sphere(Vector3(0f, 0f, 0f), 5f)
        assertEquals(Vector3(0f, 0f, 0f), sphere.center)
        assertEquals(5f, sphere.radius)
    }

    @Test
    fun `test sphere with zero radius`() {
        val sphere = Sphere(Vector3(0f, 0f, 0f), 0f)
        assertEquals(Vector3(0f, 0f, 0f), sphere.center)
        assertEquals(0f, sphere.radius)
    }

    @Test
    fun `test sphere with negative radius throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            Sphere(Vector3(0f, 0f, 0f), -5f)
        }
    }
}