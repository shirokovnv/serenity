package core.math

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Vector3TruncateTest {

    private val delta = 0.0001f

    @Test
    fun `truncate should return the same vector if length is less than maxLength`() {
        val vector = Vector3(1f, 2f, 3f)
        val maxLength = 5f
        val truncatedVector = vector.truncate(maxLength)
        assertEquals(vector, truncatedVector)
    }

    @Test
    fun `truncate should return a vector with length equal to maxLength if length is greater than maxLength`() {
        val vector = Vector3(3f, 4f, 0f) // Length = 5
        val maxLength = 2.5f
        val truncatedVector = vector.truncate(maxLength)
        assertEquals(maxLength, truncatedVector.length(), delta)
    }

    @Test
    fun `truncate should preserve the direction of the vector`() {
        val vector = Vector3(3f, 4f, 0f) // Length = 5
        val maxLength = 2.5f
        val truncatedVector = vector.truncate(maxLength)

        // Check that the angle between the original and the truncated vector is 0 (or close to zero)
        val dotProduct = vector.x * truncatedVector.x + vector.y * truncatedVector.y + vector.z * truncatedVector.z
        val magnitudeProduct = vector.length() * truncatedVector.length()
        val cosAngle = dotProduct / magnitudeProduct
        assertEquals(1.0f, cosAngle, delta) // cos(0) = 1
    }

    @Test
    fun `truncate should handle zero vector correctly`() {
        val vector = Vector3(0f, 0f, 0f)
        val maxLength = 5f
        val truncatedVector = vector.truncate(maxLength)
        assertEquals(0f, truncatedVector.length())
    }

    @Test
    fun `truncate should handle negative vector components correctly`() {
        val vector = Vector3(-3f, 4f, 0f) // Length = 5
        val maxLength = 2.5f
        val truncatedVector = vector.truncate(maxLength)
        assertEquals(maxLength, truncatedVector.length(), 0.0001f)

        // Check that the angle between the original and the truncated vector is 0 (or close to zero)
        val dotProduct = vector.x * truncatedVector.x + vector.y * truncatedVector.y + vector.z * truncatedVector.z
        val magnitudeProduct = vector.length() * truncatedVector.length()
        val cosAngle = dotProduct / magnitudeProduct
        assertEquals(1.0f, cosAngle, delta)
    }

    @Test
    fun `truncate should work with 3D vectors`() {
        val vector = Vector3(1f, 2f, 2f)  // Length = 3
        val maxLength = 1.5f
        val truncatedVector = vector.truncate(maxLength)
        assertEquals(1.5f, truncatedVector.length(), 0.0001f)

        // Check that the angle between the original and the truncated vector is 0 (or close to zero)
        val dotProduct = vector.x * truncatedVector.x + vector.y * truncatedVector.y + vector.z * truncatedVector.z
        val magnitudeProduct = vector.length() * truncatedVector.length()
        val cosAngle = dotProduct / magnitudeProduct
        assertEquals(1.0f, cosAngle, delta)
    }
}