package core.math

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class Rect3dTest {

    @Test
    fun `test default rect3d`() {
        val rect = Rect3d(Vector3(0f, 0f, 0f), Vector3(1f, 1f, 1f))
        assertEquals(Vector3(0.5f, 0.5f, 0.5f), rect.center)
        assertEquals(1f, rect.width)
        assertEquals(1f, rect.height)
        assertEquals(1f, rect.depth)
    }

    @Test
    fun `test rect3d with negative values`() {
        val rect = Rect3d(Vector3(-1f, -2f, -3f), Vector3(1f, 2f, 3f))
        assertEquals(Vector3(0f, 0f, 0f), rect.center)
        assertEquals(2f, rect.width)
        assertEquals(4f, rect.height)
        assertEquals(6f, rect.depth)
    }

    @Test
    fun `test rect3d width and height and depth calculation`() {
        val rect = Rect3d(Vector3(1f, 2f, 3f), Vector3(5f, 6f, 7f))
        assertEquals(4f, rect.width)
        assertEquals(4f, rect.height)
        assertEquals(4f, rect.depth)
    }


    @Test
    fun `test rect3d with zero width`() {
        val rect = Rect3d(Vector3(5f, 2f, 3f), Vector3(5f, 6f, 7f))
        assertEquals(0f, rect.width)
        assertEquals(4f, rect.height)
        assertEquals(4f, rect.depth)
        assertTrue(rect.width >= 0)
    }

    @Test
    fun `test rect3d with zero height`() {
        val rect = Rect3d(Vector3(1f, 2f, 3f), Vector3(5f, 2f, 7f))
        assertEquals(4f, rect.width)
        assertEquals(0f, rect.height)
        assertEquals(4f, rect.depth)
        assertTrue(rect.height >= 0)
    }
    @Test
    fun `test rect3d with zero depth`() {
        val rect = Rect3d(Vector3(1f, 2f, 3f), Vector3(5f, 6f, 3f))
        assertEquals(4f, rect.width)
        assertEquals(4f, rect.height)
        assertEquals(0f, rect.depth)
        assertTrue(rect.depth >= 0)
    }

    @Test
    fun `test size`() {
        val rect = Rect3d(Vector3(1f, 2f, 3f), Vector3(5f, 6f, 3f))
        val size = rect.size()
        assertEquals(4f, size.x)
        assertEquals(4f, size.y)
        assertEquals(0f, size.z)
    }

    @Test
    fun `test rect3d with invalid coordinates throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            Rect3d(Vector3(5f, 2f, 3f), Vector3(1f, 6f, 7f))
        }
        assertThrows(IllegalArgumentException::class.java) {
            Rect3d(Vector3(1f, 6f, 3f), Vector3(5f, 2f, 7f))
        }
        assertThrows(IllegalArgumentException::class.java) {
            Rect3d(Vector3(1f, 2f, 7f), Vector3(5f, 6f, 3f))
        }
    }
}