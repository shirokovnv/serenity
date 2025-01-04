package core.math

import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Vector2Test {
    @Test
    fun `test constructors`() {
        val vector1 = Vector2()
        assertEquals(0f, vector1.x)
        assertEquals(0f, vector1.y)

        val vector2 = Vector2(3f, 4f)
        assertEquals(3f, vector2.x)
        assertEquals(4f, vector2.y)

        val vector3 = Vector2(vector2)
        assertEquals(3f, vector3.x)
        assertEquals(4f, vector3.y)
    }

    @Test
    fun `test length`() {
        val vector = Vector2(3f, 4f)
        assertEquals(5f, vector.length())
    }
    @Test
    fun `test length squared`() {
        val vector = Vector2(3f, 4f)
        assertEquals(25f, vector.lengthSquared())
    }

    @Test
    fun `test dot product`() {
        val vector1 = Vector2(2f, 3f)
        val vector2 = Vector2(4f, -1f)
        assertEquals(5f, vector1.dot(vector2))
    }
    @Test
    fun `test normalize`() {
        val vector = Vector2(3f, 4f)
        val normalizedVector = vector.normalize()
        val delta = 0.00001f
        assertEquals(3/5f, normalizedVector.x, delta)
        assertEquals(4/5f, normalizedVector.y, delta)
        assertEquals(1f,normalizedVector.length(), delta)
    }

    @Test
    fun `test plus`() {
        val vector1 = Vector2(1f, 2f)
        val vector2 = Vector2(3f, 4f)
        val result1 = vector1 + vector2
        assertEquals(4f, result1.x)
        assertEquals(6f, result1.y)

        val result2 = vector1 + 2f
        assertEquals(3f, result2.x)
        assertEquals(4f, result2.y)
    }
    @Test
    fun `test plus assign`() {
        val vector1 = Vector2(1f, 2f)
        val vector2 = Vector2(3f, 4f)
        vector1 += vector2
        assertEquals(4f, vector1.x)
        assertEquals(6f, vector1.y)

        val vector3 = Vector2(1f, 2f)
        vector3 += 2f
        assertEquals(3f, vector3.x)
        assertEquals(4f, vector3.y)
    }

    @Test
    fun `test minus`(){
        val vector1 = Vector2(5f, 8f)
        val vector2 = Vector2(2f, 3f)
        val result1 = vector1 - vector2
        assertEquals(3f, result1.x)
        assertEquals(5f, result1.y)

        val result2 = vector1 - 2f
        assertEquals(3f, result2.x)
        assertEquals(6f, result2.y)
    }
    @Test
    fun `test minus assign`(){
        val vector1 = Vector2(5f, 8f)
        val vector2 = Vector2(2f, 3f)
        vector1 -= vector2
        assertEquals(3f, vector1.x)
        assertEquals(5f, vector1.y)

        val vector3 = Vector2(5f, 8f)
        vector3 -= 2f
        assertEquals(3f, vector3.x)
        assertEquals(6f, vector3.y)
    }
    @Test
    fun `test unary minus`(){
        val vector1 = Vector2(5f, 8f)
        vector1.unaryMinus()
        assertEquals(-5f, vector1.x)
        assertEquals(-8f, vector1.y)
    }

    @Test
    fun `test div`(){
        val vector1 = Vector2(5f, 8f)
        val vector2 = Vector2(2f, 4f)
        val result1 = vector1 / vector2
        assertEquals(2.5f, result1.x)
        assertEquals(2f, result1.y)

        val result2 = vector1 / 2f
        assertEquals(2.5f, result2.x)
        assertEquals(4f, result2.y)
    }

    @Test
    fun `test div assign`(){
        val vector1 = Vector2(5f, 8f)
        val vector2 = Vector2(2f, 4f)
        vector1 /= vector2
        assertEquals(2.5f, vector1.x)
        assertEquals(2f, vector1.y)
        val vector3 = Vector2(5f, 8f)
        vector3 /= 2f
        assertEquals(2.5f, vector3.x)
        assertEquals(4f, vector3.y)
    }

    @Test
    fun `test times`(){
        val vector1 = Vector2(5f, 8f)
        val vector2 = Vector2(2f, 4f)
        val result1 = vector1 * vector2
        assertEquals(10f, result1.x)
        assertEquals(32f, result1.y)

        val result2 = vector1 * 2f
        assertEquals(10f, result2.x)
        assertEquals(16f, result2.y)
    }
    @Test
    fun `test times assign`(){
        val vector1 = Vector2(5f, 8f)
        val vector2 = Vector2(2f, 4f)
        vector1 *= vector2
        assertEquals(10f, vector1.x)
        assertEquals(32f, vector1.y)

        val vector3 = Vector2(5f, 8f)
        vector3 *= 2f
        assertEquals(10f, vector3.x)
        assertEquals(16f, vector3.y)
    }

    @Test
    fun `test equals`() {
        val vector1 = Vector2(1f, 2f)
        val vector2 = Vector2(1f, 2f)
        val vector3 = Vector2(3f, 4f)

        assertEquals(vector1, vector2)
        assertNotEquals(vector1, vector3)
    }

    @Test
    fun `test hashCode`() {
        val vector1 = Vector2(1f, 2f)
        val vector2 = Vector2(1f, 2f)

        assertEquals(vector1.hashCode(), vector2.hashCode())
    }

    @Test
    fun `test to string`() {
        val vector = Vector2(1f, 2f)
        assertEquals("[1.0,2.0]", vector.toString())
    }
}