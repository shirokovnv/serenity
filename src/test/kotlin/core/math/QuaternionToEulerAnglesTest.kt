package core.math

import core.math.extensions.toRadians
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class QuaternionToEulerAnglesTest {
    private val epsilon = 0.001f

    @Test
    fun `getEulerAngles - multiple test cases`() {
        testEulerAngles(
            Quaternion.fromEulerAngles(0f, 0f, 0f),
            Vector3(0f, 0f, 0f),
            "Zero rotation"
        )

        testEulerAngles(
            Quaternion.fromAxisAngle(1f, 0f, 0f, 90f.toRadians()),
            Vector3(PI.toFloat() / 2f, 0f,  0f),
            "90 degrees rotation around X"
        )

        testEulerAngles(
            Quaternion.fromAxisAngle(0f, 1f, 0f, 90f.toRadians()),
            Vector3(0f, PI.toFloat() / 2f, 0f),
            "90 degrees rotation around Y"
        )

        testEulerAngles(
            Quaternion.fromAxisAngle(0f, 0f, 1f, 90f.toRadians()),
            Vector3(0f, 0f, PI.toFloat() / 2f),
            "90 degrees rotation around Z"
        )

        testEulerAngles(
            Quaternion.fromEulerAngles(45f.toRadians(), 30f.toRadians(), 60f.toRadians()),
            Vector3(45f.toRadians(), 30f.toRadians(), 60f.toRadians()),
            "Combined rotation"
        )

        testEulerAngles(
            Quaternion.fromEulerAngles(180f.toRadians(), 0f, 0f),
            Vector3(-PI.toFloat(), 0f, 0f),
            "180 degrees rotation around X"
        )

        testEulerAngles(
            Quaternion.fromEulerAngles(0f, 180f.toRadians(), 0f),
            Vector3(PI.toFloat(), 0f, PI.toFloat()),
            "180 degrees rotation around Y"
        )

        testEulerAngles(
            Quaternion.fromEulerAngles(0f, 0f, 180f.toRadians()),
            Vector3(0f, 0f, -PI.toFloat()),
            "180 degrees rotation around Z"
        )
    }

    private fun testEulerAngles(quaternion: Quaternion, expected: Vector3, message: String) {
        val actual = quaternion.toEulerAngles()
        println("$message , expected = $expected, actual = $actual")
        assertEquals(expected.x, actual.x, epsilon, "$message - X")
        assertEquals(expected.y, actual.y, epsilon, "$message - Y")
        assertEquals(expected.z, actual.z, epsilon, "$message - Z")
    }
}