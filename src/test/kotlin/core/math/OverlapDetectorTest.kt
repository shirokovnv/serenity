package core.math

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class OverlapDetectorTest {
    @Test
    fun `test contains rect2d`() {
        val rectA = Rect2d(Vector2(0f, 0f), Vector2(5f, 5f))
        val rectB = Rect2d(Vector2(1f, 1f), Vector2(4f, 4f))
        val rectC = Rect2d(Vector2(6f, 6f), Vector2(10f, 10f))
        assertTrue(OverlapDetector.contains(rectA, rectB))
        assertFalse(OverlapDetector.contains(rectA, rectC))
    }

    @Test
    fun `test contains rect3d`() {
        val rectA = Rect3d(Vector3(0f, 0f, 0f), Vector3(5f, 5f, 5f))
        val rectB = Rect3d(Vector3(1f, 1f, 1f), Vector3(4f, 4f, 4f))
        val rectC = Rect3d(Vector3(6f, 6f, 6f), Vector3(10f, 10f, 10f))
        assertTrue(OverlapDetector.contains(rectA, rectB))
        assertFalse(OverlapDetector.contains(rectA, rectC))
    }

    @Test
    fun `test contains sphere`() {
        val sphereA = Sphere(Vector3(0f, 0f, 0f), 5f)
        val sphereB = Sphere(Vector3(0f, 0f, 0f), 2f)
        val sphereC = Sphere(Vector3(5f, 0f, 0f), 2f)
        val sphereD = Sphere(Vector3(3f, 0f, 0f), 1f)
        assertTrue(OverlapDetector.contains(sphereA, sphereB))
        assertFalse(OverlapDetector.contains(sphereA, sphereC))
        assertFalse(OverlapDetector.contains(sphereC, sphereA))
        assertTrue(OverlapDetector.contains(sphereA, sphereD))
    }

    @Test
    fun `test rect3d contains sphere`() {
        val rect = Rect3d(Vector3(0f, 0f, 0f), Vector3(10f, 10f, 10f))
        val sphere1 = Sphere(Vector3(5f, 5f, 5f), 2f) // inside
        val sphere2 = Sphere(Vector3(15f, 5f, 5f), 2f) // outside
        val sphere3 = Sphere(Vector3(0f,0f,0f), 0f) // intersects
        val sphere4 = Sphere(Vector3(10f,10f,10f), 0f) // intersects
        assertTrue(OverlapDetector.contains(rect, sphere1))
        assertFalse(OverlapDetector.contains(rect, sphere2))
        assertTrue(OverlapDetector.contains(rect, sphere3))
        assertTrue(OverlapDetector.contains(rect, sphere4))
    }

    @Test
    fun `test sphere contains rect3d`() {
        val sphere = Sphere(Vector3(5f, 5f, 5f), 10f)
        val rect1 = Rect3d(Vector3(1f, 1f, 1f), Vector3(3f, 3f, 3f)) // inside
        val rect2 = Rect3d(Vector3(10f, 10f, 10f), Vector3(12f, 12f, 12f)) // intersects
        val rect3 = Rect3d(Vector3(20f, 20f, 20f), Vector3(22f, 22f, 22f)) // outside
        assertTrue(OverlapDetector.contains(sphere, rect1))
        assertFalse(OverlapDetector.contains(sphere, rect2))
        assertFalse(OverlapDetector.contains(sphere, rect3))
    }
}