package core.math

import core.math.extensions.toRadians
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class QuaternionCompanionTest {
    private val epsilon = 0.00001f

    @Test
    fun `fromAxisAngle - zero rotation`() {
        val q = Quaternion.fromAxisAngle(1f, 0f, 0f, 0f)
        assertEquals(1f, q.w, epsilon)
        assertEquals(0f, q.x, epsilon)
        assertEquals(0f, q.y, epsilon)
        assertEquals(0f, q.z, epsilon)
    }

    @Test
    fun `fromAxisAngle - 90 degrees rotation around X`() {
        val q = Quaternion.fromAxisAngle(1f, 0f, 0f, 90f.toRadians())
        assertEquals(sqrt(0.5f), q.w, epsilon)
        assertEquals(sqrt(0.5f), q.x, epsilon)
        assertEquals(0f, q.y, epsilon)
        assertEquals(0f, q.z, epsilon)
    }

    @Test
    fun `fromAxisAngle - 180 degrees rotation around Y`() {
        val q = Quaternion.fromAxisAngle(0f, 1f, 0f, 180f.toRadians())
        assertEquals(0f, q.w, epsilon)
        assertEquals(0f, q.x, epsilon)
        assertEquals(1f, q.y, epsilon)
        assertEquals(0f, q.z, epsilon)
    }

    @Test
    fun `fromAxisAngle - 45 degrees rotation around arbitrary axis`() {
        val q = Quaternion.fromAxisAngle(1f, 1f, 1f, 45f.toRadians())
        assertEquals(0.9238795f, q.w, epsilon)
        assertEquals(0.2209424f, q.x, epsilon)
        assertEquals(0.2209424f, q.y, epsilon)
        assertEquals(0.2209424f, q.z, epsilon)
    }

    @Test
    fun `fromEulerAngles - zero rotation`() {
        val q = Quaternion.fromEulerAngles(0f, 0f, 0f)
        assertEquals(1f, q.w, epsilon)
        assertEquals(0f, q.x, epsilon)
        assertEquals(0f, q.y, epsilon)
        assertEquals(0f, q.z, epsilon)
    }

    @Test
    fun `fromEulerAngles - 90 degrees around X`() {
        val q = Quaternion.fromEulerAngles(90f.toRadians(), 0f, 0f)
        assertEquals(sqrt(0.5f), q.w, epsilon)
        assertEquals(sqrt(0.5f), q.x, epsilon)
        assertEquals(0f, q.y, epsilon)
        assertEquals(0f, q.z, epsilon)
    }

    @Test
    fun `fromEulerAngles - 90 degrees around Y`() {
        val q = Quaternion.fromEulerAngles(0f, 90f.toRadians(), 0f)
        assertEquals(sqrt(0.5f), q.w, epsilon)
        assertEquals(0f, q.x, epsilon)
        assertEquals(sqrt(0.5f), q.y, epsilon)
        assertEquals(0f, q.z, epsilon)
    }

    @Test
    fun `fromEulerAngles - 90 degrees around Z`() {
        val q = Quaternion.fromEulerAngles(0f, 0f, 90f.toRadians())
        assertEquals(sqrt(0.5f), q.w, epsilon)
        assertEquals(0f, q.x, epsilon)
        assertEquals(0f, q.y, epsilon)
        assertEquals(sqrt(0.5f), q.z, epsilon)
    }

    @Test
    fun `fromEulerAngles - combined rotation`() {
        val q = Quaternion.fromEulerAngles(45f.toRadians(), 30f.toRadians(), 60f.toRadians())
        assertEquals(0.8223631f, q.w, epsilon)
        assertEquals(0.2005621f, q.x, epsilon)
        assertEquals(0.3919038f, q.y, epsilon)
        assertEquals(0.3604234f, q.z, epsilon)
    }
}