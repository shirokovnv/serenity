package core.math

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class Vector3ClampTest {

    @Test
    fun `clamp should not change values within min-max range`() {
        val vector = Vector3(1f, 2f, 3f)
        vector.clamp(1f, 3f)

        assertEquals(vector.x, 1f)
        assertEquals(vector.y, 2f)
        assertEquals(vector.z, 3f)
    }

    @Test
    fun `clamp should set minimum and maximum values outside range`() {
        val vector = Vector3(1f, 2f, 3f)
        vector.clamp(1.5f, 2.5f)

        assertEquals(vector.x, 1.5f)
        assertEquals(vector.y, 2f)
        assertEquals(vector.z, 2.5f)
    }

    @Test
    fun `clamp should throw exception when min greater than max`() {
        assertThrows<IllegalArgumentException> {
            val vector = Vector3(1f, 2f, 3f)
            vector.clamp(2f, 1f)
        }
    }
}