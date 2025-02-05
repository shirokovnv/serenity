package core.math

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class IntersectionDetectorTest {

    @Test
    fun `test rect2d intersects`() {
        val rectA = Rect2d(Vector2(0f, 0f), Vector2(2f, 2f))
        val rectB = Rect2d(Vector2(1f, 1f), Vector2(3f, 3f))
        assertTrue(IntersectionDetector.intersects(rectA, rectB))
    }

    @Test
    fun `test rect2d not intersects`() {
        val rectA = Rect2d(Vector2(0f, 0f), Vector2(1f, 1f))
        val rectB = Rect2d(Vector2(2f, 2f), Vector2(3f, 3f))
        assertFalse(IntersectionDetector.intersects(rectA, rectB))
    }

    @Test
    fun `test rect3d intersects`() {
        val rectA = Rect3d(Vector3(0f, 0f, 0f), Vector3(2f, 2f, 2f))
        val rectB = Rect3d(Vector3(1f, 1f, 1f), Vector3(3f, 3f, 3f))
        assertTrue(IntersectionDetector.intersects(rectA, rectB))
    }

    @Test
    fun `test rect3d not intersects`() {
        val rectA = Rect3d(Vector3(0f, 0f, 0f), Vector3(1f, 1f, 1f))
        val rectB = Rect3d(Vector3(2f, 2f, 2f), Vector3(3f, 3f, 3f))
        assertFalse(IntersectionDetector.intersects(rectA, rectB))
    }

    @Test
    fun `test sphere intersects`() {
        val sphereA = Sphere(Vector3(0f, 0f, 0f), 1f)
        val sphereB = Sphere(Vector3(1f, 0f, 0f), 1f)
        assertTrue(IntersectionDetector.intersects(sphereA, sphereB))
    }

    @Test
    fun `test sphere not intersects`() {
        val sphereA = Sphere(Vector3(0f, 0f, 0f), 1f)
        val sphereB = Sphere(Vector3(3f, 0f, 0f), 1f)
        assertFalse(IntersectionDetector.intersects(sphereA, sphereB))
    }

    @Test
    fun `test rect3d sphere intersects`() {
        val rect = Rect3d(Vector3(0f, 0f, 0f), Vector3(2f, 2f, 2f))
        val sphere = Sphere(Vector3(1f, 1f, 1f), 1f)
        assertTrue(IntersectionDetector.intersects(rect, sphere))
    }

    @Test
    fun `test rect3d sphere not intersects`() {
        val rect = Rect3d(Vector3(0f, 0f, 0f), Vector3(1f, 1f, 1f))
        val sphere = Sphere(Vector3(3f, 3f, 3f), 1f)
        assertFalse(IntersectionDetector.intersects(rect, sphere))
    }

    @Test
    fun `test rect3d point intersects`() {
        val rect = Rect3d(Vector3(0f, 0f, 0f), Vector3(1f, 1f, 1f))
        val point = Vector3(0.5f, 0.5f, 0.5f)
        assertTrue(IntersectionDetector.intersects(rect, point))
    }

    @Test
    fun `test rect3d point not intersects`() {
        val rect = Rect3d(Vector3(0f, 0f, 0f), Vector3(1f, 1f, 1f))
        val point = Vector3(1.5f, 1.5f, 1.5f)
        assertFalse(IntersectionDetector.intersects(rect, point))
    }

    @Test
    fun `test sphere point intersects`() {
        val sphere = Sphere(Vector3(0f), 10f)
        val point = Vector3(1f)
        assertTrue(IntersectionDetector.intersects(sphere, point))
    }

    @Test
    fun `test sphere point not intersects`() {
        val sphere = Sphere(Vector3(0f), 10f)
        val point = Vector3(12f)
        assertFalse(IntersectionDetector.intersects(sphere, point))
    }
}