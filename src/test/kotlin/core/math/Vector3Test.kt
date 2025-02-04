package core.math

import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.math.sqrt

class Vector3Test {

    @Test
    fun `test constructors`() {
        val vector1 = Vector3()
        assertEquals(0f, vector1.x)
        assertEquals(0f, vector1.y)
        assertEquals(0f, vector1.z)

        val vector2 = Vector3(1f)
        assertEquals(1f, vector2.x)
        assertEquals(1f, vector2.y)
        assertEquals(1f, vector2.z)

        val vector3 = Vector3(2f, 3f, 4f)
        assertEquals(2f, vector3.x)
        assertEquals(3f, vector3.y)
        assertEquals(4f, vector3.z)

        val vector4 = Vector3(vector3)
        assertEquals(2f, vector4.x)
        assertEquals(3f, vector4.y)
        assertEquals(4f, vector4.z)
    }

    @Test
    fun `test length`() {
        val vector = Vector3(3f, 4f, 5f)
        assertEquals(sqrt(50f), vector.length())
    }

    @Test
    fun `test length squared`() {
        val vector = Vector3(3f, 4f, 5f)
        assertEquals(50f, vector.lengthSquared())
    }

    @Test
    fun `test dot product`() {
        val vector1 = Vector3(1f, 2f, 3f)
        val vector2 = Vector3(4f, 5f, 6f)
        assertEquals(32f, vector1.dot(vector2))
    }

    @Test
    fun `test cross product`() {
        val vector1 = Vector3(1f, 2f, 3f)
        val vector2 = Vector3(4f, 5f, 6f)
        val result = vector1.cross(vector2)
        assertEquals(-3f, result.x)
        assertEquals(6f, result.y)
        assertEquals(-3f, result.z)
    }

    @Test
    fun `test normalize`() {
        val vector = Vector3(3f, 4f, 5f)
        val normalizedVector = vector.normalize()
        val length = sqrt(50f)
        val delta = 0.00001f
        assertEquals(3/length, normalizedVector.x, delta)
        assertEquals(4/length, normalizedVector.y, delta)
        assertEquals(5/length, normalizedVector.z, delta)
        assertEquals(1f,normalizedVector.length(), delta)
    }

    @Test
    fun `test plus`() {
        val vector1 = Vector3(1f, 2f, 3f)
        val vector2 = Vector3(4f, 5f, 6f)
        val result1 = vector1 + vector2
        assertEquals(5f, result1.x)
        assertEquals(7f, result1.y)
        assertEquals(9f, result1.z)

        val result2 = vector1 + 2f
        assertEquals(3f, result2.x)
        assertEquals(4f, result2.y)
        assertEquals(5f, result2.z)
    }

    @Test
    fun `test plus assign`() {
        val vector1 = Vector3(1f, 2f, 3f)
        val vector2 = Vector3(4f, 5f, 6f)
        vector1 += vector2
        assertEquals(5f, vector1.x)
        assertEquals(7f, vector1.y)
        assertEquals(9f, vector1.z)
        val vector3 = Vector3(1f, 2f, 3f)
        vector3 += 2f
        assertEquals(3f, vector3.x)
        assertEquals(4f, vector3.y)
        assertEquals(5f, vector3.z)
    }

    @Test
    fun `test minus`() {
        val vector1 = Vector3(5f, 8f, 11f)
        val vector2 = Vector3(2f, 3f, 4f)
        val result1 = vector1 - vector2
        assertEquals(3f, result1.x)
        assertEquals(5f, result1.y)
        assertEquals(7f, result1.z)

        val result2 = vector1 - 2f
        assertEquals(3f, result2.x)
        assertEquals(6f, result2.y)
        assertEquals(9f, result2.z)
    }

    @Test
    fun `test minus assign`() {
        val vector1 = Vector3(5f, 8f, 11f)
        val vector2 = Vector3(2f, 3f, 4f)
        vector1 -= vector2
        assertEquals(3f, vector1.x)
        assertEquals(5f, vector1.y)
        assertEquals(7f, vector1.z)

        val vector3 = Vector3(5f, 8f, 11f)
        vector3 -= 2f
        assertEquals(3f, vector3.x)
        assertEquals(6f, vector3.y)
        assertEquals(9f, vector3.z)
    }

    @Test
    fun `test unary minus`() {
        var vector1 = Vector3(5f, 8f, 11f)
        vector1 = vector1.unaryMinus()
        assertEquals(-5f, vector1.x)
        assertEquals(-8f, vector1.y)
        assertEquals(-11f, vector1.z)
    }

    @Test
    fun `test div`(){
        val vector1 = Vector3(5f, 8f, 11f)
        val vector2 = Vector3(2f, 4f, 5f)
        val result1 = vector1 / vector2
        assertEquals(2.5f, result1.x)
        assertEquals(2f, result1.y)
        assertEquals(2.2f, result1.z)

        val result2 = vector1 / 2f
        assertEquals(2.5f, result2.x)
        assertEquals(4f, result2.y)
        assertEquals(5.5f, result2.z)
    }

    @Test
    fun `test div assign`(){
        val vector1 = Vector3(5f, 8f, 11f)
        val vector2 = Vector3(2f, 4f, 5f)
        vector1 /= vector2
        assertEquals(2.5f, vector1.x)
        assertEquals(2f, vector1.y)
        assertEquals(2.2f, vector1.z)

        val vector3 = Vector3(5f, 8f, 11f)
        vector3 /= 2f
        assertEquals(2.5f, vector3.x)
        assertEquals(4f, vector3.y)
        assertEquals(5.5f, vector3.z)
    }

    @Test
    fun `test times`(){
        val vector1 = Vector3(5f, 8f, 11f)
        val vector2 = Vector3(2f, 4f, 5f)
        val result1 = vector1 * vector2
        assertEquals(10f, result1.x)
        assertEquals(32f, result1.y)
        assertEquals(55f, result1.z)

        val result2 = vector1 * 2f
        assertEquals(10f, result2.x)
        assertEquals(16f, result2.y)
        assertEquals(22f, result2.z)
    }

    @Test
    fun `test times assign`(){
        val vector1 = Vector3(5f, 8f, 11f)
        val vector2 = Vector3(2f, 4f, 5f)
        vector1 *= vector2
        assertEquals(10f, vector1.x)
        assertEquals(32f, vector1.y)
        assertEquals(55f, vector1.z)

        val vector3 = Vector3(5f, 8f, 11f)
        vector3 *= 2f
        assertEquals(10f, vector3.x)
        assertEquals(16f, vector3.y)
        assertEquals(22f, vector3.z)
    }

    @Test
    fun `test component1_2_3`() {
        val vector3 = Vector3(1f, 2f, 3f)
        val (x, y, z) = vector3
        assertEquals(1f, x)
        assertEquals(2f, y)
        assertEquals(3f, z)
    }

    @Test
    fun `test equals`() {
        val vector1 = Vector3(1f, 2f, 3f)
        val vector2 = Vector3(1f, 2f, 3f)
        val vector3 = Vector3(4f, 5f, 6f)

        assertEquals(vector1, vector2)
        assertNotEquals(vector1, vector3)
    }

    @Test
    fun `test hashCode`() {
        val vector1 = Vector3(1f, 2f, 3f)
        val vector2 = Vector3(1f, 2f, 3f)

        assertEquals(vector1.hashCode(), vector2.hashCode())
    }

    @Test
    fun `test to string`() {
        val vector = Vector3(1f, 2f, 3f)
        assertEquals("[1.0,2.0,3.0]", vector.toString())
    }

    @Test
    fun `get should return correct component for valid index`() {
        val vector = Vector3(1.0f, 2.0f, 3.0f)
        assertEquals(1.0f, vector[0])
        assertEquals(2.0f, vector[1])
        assertEquals(3.0f, vector[2])
    }

    @Test
    fun `get should throw IndexOutOfBoundsException for invalid index`() {
        val vector = Vector3(1.0f, 2.0f, 3.0f)
        assertThrows<IndexOutOfBoundsException> { vector[-1] }
        assertThrows<IndexOutOfBoundsException> { vector[3] }
        assertThrows<IndexOutOfBoundsException> { vector[4] }
    }
}