package core.math

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class Rect2dTest {

    @Test
    fun `test default rect2d`() {
        val rect = Rect2d(Vector2(0f, 0f), Vector2(1f, 1f))
        assertEquals(Vector2(0.5f, 0.5f), rect.center)
        assertEquals(1f, rect.width)
        assertEquals(1f, rect.height)
    }


    @Test
    fun `test rect2d with negative values`() {
        val rect = Rect2d(Vector2(-1f, -2f), Vector2(1f, 2f))
        assertEquals(Vector2(0f, 0f), rect.center)
        assertEquals(2f, rect.width)
        assertEquals(4f, rect.height)
    }


    @Test
    fun `test rect2d width and height calculation`() {
        val rect = Rect2d(Vector2(1f, 2f), Vector2(5f, 6f))
        assertEquals(4f, rect.width)
        assertEquals(4f, rect.height)
    }


    @Test
    fun `test rect2d with zero width`() {
        val rect = Rect2d(Vector2(5f, 2f), Vector2(5f, 6f))
        assertEquals(0f, rect.width)
        assertEquals(4f, rect.height)
        assertTrue(rect.width >= 0)
    }

    @Test
    fun `test rect2d with zero height`() {
        val rect = Rect2d(Vector2(1f, 2f), Vector2(5f, 2f))
        assertEquals(4f, rect.width)
        assertEquals(0f, rect.height)
        assertTrue(rect.height >= 0)
    }

    @Test
    fun `test rect2d with invalid coordinates throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            Rect2d(Vector2(5f, 2f), Vector2(1f, 6f))
        }
        assertThrows(IllegalArgumentException::class.java) {
            Rect2d(Vector2(1f, 6f), Vector2(5f, 2f))
        }
    }
}