package core.math

import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.math.sqrt

class QuaternionTest {
    @Test
    fun `test constructors`(){
        val quaternion1 = Quaternion(1f,2f,3f,4f)
        assertEquals(1f, quaternion1.x)
        assertEquals(2f, quaternion1.y)
        assertEquals(3f, quaternion1.z)
        assertEquals(4f, quaternion1.w)

        val vector = Vector3(1f, 2f, 3f)
        val quaternion2 = Quaternion(vector, 4f)
        assertEquals(1f, quaternion2.x)
        assertEquals(2f, quaternion2.y)
        assertEquals(3f, quaternion2.z)
        assertEquals(4f, quaternion2.w)
    }

    @Test
    fun `test length`() {
        val quaternion = Quaternion(1f, 2f, 3f, 4f)
        assertEquals(sqrt(30f), quaternion.length())
    }

    @Test
    fun `test length squared`() {
        val quaternion = Quaternion(1f, 2f, 3f, 4f)
        assertEquals(30f, quaternion.lengthSquared())
    }

    @Test
    fun `test normalize`() {
        val quaternion = Quaternion(1f, 2f, 3f, 4f)
        val normalized = quaternion.normalize()
        val length = sqrt(30f)
        val delta = 0.00001f

        assertEquals(1/length, normalized.x, delta)
        assertEquals(2/length, normalized.y, delta)
        assertEquals(3/length, normalized.z, delta)
        assertEquals(4/length, normalized.w, delta)
        assertEquals(1f, normalized.length(), delta)
    }

    @Test
    fun `test conjugate`() {
        val quaternion = Quaternion(1f, 2f, 3f, 4f)
        val conjugate = quaternion.conjugate()
        assertEquals(-1f, conjugate.x)
        assertEquals(-2f, conjugate.y)
        assertEquals(-3f, conjugate.z)
        assertEquals(4f, conjugate.w)
    }

    @Test
    fun `test plus`(){
        val quaternion1 = Quaternion(1f, 2f, 3f, 4f)
        val quaternion2 = Quaternion(4f, 3f, 2f, 1f)
        val result1 = quaternion1 + quaternion2
        assertEquals(5f, result1.x)
        assertEquals(5f, result1.y)
        assertEquals(5f, result1.z)
        assertEquals(5f, result1.w)

        val result2 = quaternion1 + 2f
        assertEquals(3f, result2.x)
        assertEquals(4f, result2.y)
        assertEquals(5f, result2.z)
        assertEquals(6f, result2.w)
    }

    @Test
    fun `test plus assign`(){
        val quaternion1 = Quaternion(1f, 2f, 3f, 4f)
        val quaternion2 = Quaternion(4f, 3f, 2f, 1f)
        quaternion1 += quaternion2
        assertEquals(5f, quaternion1.x)
        assertEquals(5f, quaternion1.y)
        assertEquals(5f, quaternion1.z)
        assertEquals(5f, quaternion1.w)

        val quaternion3 = Quaternion(1f, 2f, 3f, 4f)
        quaternion3 += 2f
        assertEquals(3f, quaternion3.x)
        assertEquals(4f, quaternion3.y)
        assertEquals(5f, quaternion3.z)
        assertEquals(6f, quaternion3.w)
    }

    @Test
    fun `test minus`() {
        val quaternion1 = Quaternion(5f, 8f, 11f, 14f)
        val quaternion2 = Quaternion(2f, 3f, 4f, 5f)
        val result1 = quaternion1 - quaternion2
        assertEquals(3f, result1.x)
        assertEquals(5f, result1.y)
        assertEquals(7f, result1.z)
        assertEquals(9f, result1.w)

        val result2 = quaternion1 - 2f
        assertEquals(3f, result2.x)
        assertEquals(6f, result2.y)
        assertEquals(9f, result2.z)
        assertEquals(12f, result2.w)
    }

    @Test
    fun `test minus assign`() {
        val quaternion1 = Quaternion(5f, 8f, 11f, 14f)
        val quaternion2 = Quaternion(2f, 3f, 4f, 5f)
        quaternion1 -= quaternion2
        assertEquals(3f, quaternion1.x)
        assertEquals(5f, quaternion1.y)
        assertEquals(7f, quaternion1.z)
        assertEquals(9f, quaternion1.w)

        val quaternion3 = Quaternion(5f, 8f, 11f, 14f)
        quaternion3 -= 2f
        assertEquals(3f, quaternion3.x)
        assertEquals(6f, quaternion3.y)
        assertEquals(9f, quaternion3.z)
        assertEquals(12f, quaternion3.w)
    }

    @Test
    fun `test unary minus`() {
        var quaternion = Quaternion(5f, 8f, 11f, 14f)
        quaternion = quaternion.unaryMinus()
        assertEquals(-5f, quaternion.x)
        assertEquals(-8f, quaternion.y)
        assertEquals(-11f, quaternion.z)
        assertEquals(-14f, quaternion.w)
    }

    @Test
    fun `test div`(){
        val quaternion1 = Quaternion(5f, 8f, 11f, 14f)
        val quaternion2 = Quaternion(2f, 4f, 5f, 7f)
        val result1 = quaternion1 / quaternion2
        assertEquals(2.5f, result1.x)
        assertEquals(2f, result1.y)
        assertEquals(2.2f, result1.z)
        assertEquals(2f, result1.w)

        val result2 = quaternion1 / 2f
        assertEquals(2.5f, result2.x)
        assertEquals(4f, result2.y)
        assertEquals(5.5f, result2.z)
        assertEquals(7f, result2.w)
    }

    @Test
    fun `test div assign`(){
        val quaternion1 = Quaternion(5f, 8f, 11f, 14f)
        val quaternion2 = Quaternion(2f, 4f, 5f, 7f)
        quaternion1 /= quaternion2
        assertEquals(2.5f, quaternion1.x)
        assertEquals(2f, quaternion1.y)
        assertEquals(2.2f, quaternion1.z)
        assertEquals(2f, quaternion1.w)

        val quaternion3 = Quaternion(5f, 8f, 11f, 14f)
        quaternion3 /= 2f
        assertEquals(2.5f, quaternion3.x)
        assertEquals(4f, quaternion3.y)
        assertEquals(5.5f, quaternion3.z)
        assertEquals(7f, quaternion3.w)
    }

    @Test
    fun `test times with quaternion`() {
        val quaternion1 = Quaternion(1f, 2f, 3f, 4f)
        val quaternion2 = Quaternion(4f, 3f, 2f, 1f)
        val result = quaternion1 * quaternion2

        assertEquals(12f, result.x, 0.00001f)
        assertEquals(24f, result.y, 0.00001f)
        assertEquals(6f, result.z, 0.00001f)
        assertEquals(-12f, result.w, 0.00001f)
    }


    @Test
    fun `test times with vector3`() {
        val quaternion = Quaternion(1f, 2f, 3f, 4f)
        val vector = Vector3(1f, 2f, 3f)
        val result = quaternion * vector
        assertEquals(4f, result.x, 0.00001f)
        assertEquals(8f, result.y, 0.00001f)
        assertEquals(12f, result.z, 0.00001f)
        assertEquals(-14f, result.w, 0.00001f)
    }


    @Test
    fun `test times with scalar`() {
        val quaternion = Quaternion(1f, 2f, 3f, 4f)
        val result = quaternion * 2f
        assertEquals(2f, result.x, 0.00001f)
        assertEquals(4f, result.y, 0.00001f)
        assertEquals(6f, result.z, 0.00001f)
        assertEquals(8f, result.w, 0.00001f)
    }

    @Test
    fun `test timesAssign with quaternion`() {
        val quaternion1 = Quaternion(1f, 2f, 3f, 4f)
        val quaternion2 = Quaternion(4f, 3f, 2f, 1f)
        quaternion1 *= quaternion2

        assertEquals(12f, quaternion1.x, 0.00001f)
        assertEquals(24f, quaternion1.y, 0.00001f)
        assertEquals(6f, quaternion1.z, 0.00001f)
        assertEquals(-12f, quaternion1.w, 0.00001f)
    }

    @Test
    fun `test timesAssign with vector3`() {
        val quaternion = Quaternion(1f, 2f, 3f, 4f)
        val vector = Vector3(1f, 2f, 3f)
        quaternion *= vector

        assertEquals(4f, quaternion.x, 0.00001f)
        assertEquals(8f, quaternion.y, 0.00001f)
        assertEquals(12f, quaternion.z, 0.00001f)
        assertEquals(-14f, quaternion.w, 0.00001f)
    }

    @Test
    fun `test timesAssign with scalar`() {
        val quaternion = Quaternion(1f, 2f, 3f, 4f)
        quaternion *= 2f
        assertEquals(2f, quaternion.x, 0.00001f)
        assertEquals(4f, quaternion.y, 0.00001f)
        assertEquals(6f, quaternion.z, 0.00001f)
        assertEquals(8f, quaternion.w, 0.00001f)
    }

    @Test
    fun `test xyz`() {
        val quaternion = Quaternion(1f, 2f, 3f, 4f)
        val vector = quaternion.xyz()
        assertEquals(1f, vector.x)
        assertEquals(2f, vector.y)
        assertEquals(3f, vector.z)
    }

    @Test
    fun `test component1_2_3_4`() {
        val quaternion = Quaternion(1f, 2f, 3f, 4f)
        val (x, y, z, w) = quaternion
        assertEquals(1f, x)
        assertEquals(2f, y)
        assertEquals(3f, z)
        assertEquals(4f, w)
    }

    @Test
    fun `test dot`() {
        val quaternion1 = Quaternion(1f, 2f, 3f, 4f)
        val quaternion2 = Quaternion(4f, 3f, 2f, 1f)
        val dot = quaternion1.dot(quaternion2)
        assertEquals(20f, dot)
    }

    @Test
    fun `test equals`() {
        val quaternion1 = Quaternion(1f, 2f, 3f, 4f)
        val quaternion2 = Quaternion(1f, 2f, 3f, 4f)
        val quaternion3 = Quaternion(5f, 6f, 7f, 8f)

        assertEquals(quaternion1, quaternion2)
        assertNotEquals(quaternion1, quaternion3)
    }

    @Test
    fun `test hashCode`() {
        val quaternion1 = Quaternion(1f, 2f, 3f, 4f)
        val quaternion2 = Quaternion(1f, 2f, 3f, 4f)

        assertEquals(quaternion1.hashCode(), quaternion2.hashCode())
    }

    @Test
    fun `test toString`() {
        val quaternion = Quaternion(1f, 2f, 3f, 4f)
        assertEquals("[1.0,2.0,3.0,4.0]", quaternion.toString())
    }
}