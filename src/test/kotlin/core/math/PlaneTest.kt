package core.math

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class PlaneTest {

    @Test
    fun `test create plane from point and normal`() {
        val point = Vector3(1f, 2f, 3f)
        val normal = Vector3(1f, 0f, 0f)
        val plane = Plane.fromPoint(point, normal)
        val expectedNormal = Vector3(1f, 0f, 0f)
        assertEquals(expectedNormal, plane.normal)
        assertEquals(1f, plane.distance)
    }

    @Test
    fun `test create plane from point and non-uniform normal`() {
        val point = Vector3(1f, 2f, 3f)
        val normal = Vector3(2f, 0f, 0f)
        val plane = Plane.fromPoint(point, normal)
        val expectedNormal = Vector3(1f, 0f, 0f)
        assertEquals(expectedNormal, plane.normal)
        assertEquals(1f, plane.distance)
    }

    @Test
    fun `test normalize plane`() {
        val normal = Vector3(2f, 0f, 0f)
        val plane = Plane(normal, 4f)
        val normalizedPlane = plane.normalize()
        val expectedNormal = Vector3(1f, 0f, 0f)
        assertEquals(expectedNormal, normalizedPlane.normal)
        assertEquals(2f, normalizedPlane.distance)
    }

    @Test
    fun `test normalize plane when normal is already normalized`() {
        val normal = Vector3(1f, 0f, 0f)
        val plane = Plane(normal, 4f)
        val normalizedPlane = plane.normalize()
        val expectedNormal = Vector3(1f, 0f, 0f)
        assertEquals(expectedNormal, normalizedPlane.normal)
        assertEquals(4f, normalizedPlane.distance)
    }

    @Test
    fun `test signed distance is positive`() {
        val normal = Vector3(1f, 0f, 0f)
        val plane = Plane(normal, 2f)
        val point = Vector3(3f, 0f, 0f)
        assertEquals(5f, plane.signedDistance(point))
    }

    @Test
    fun `test signed distance is equal to zero`() {
        val normal = Vector3(1f, 0f, 0f)
        val plane = Plane(normal, 0f)
        val point = Vector3(0f, 0f, 0f)
        assertEquals(0f, plane.signedDistance(point))
    }

    @Test
    fun `test plane equals with same properties`() {
        val normal1 = Vector3(1f, 0f, 0f)
        val plane1 = Plane(normal1, 2f)
        val normal2 = Vector3(1f, 0f, 0f)
        val plane2 = Plane(normal2, 2f)
        assertEquals(plane1, plane2)
        assertEquals(plane1.hashCode(), plane2.hashCode())
    }

    @Test
    fun `test plane not equals with different normal`() {
        val normal1 = Vector3(1f, 0f, 0f)
        val plane1 = Plane(normal1, 2f)
        val normal2 = Vector3(0f, 1f, 0f)
        val plane2 = Plane(normal2, 2f)
        assertNotEquals(plane1, plane2)
        assertNotEquals(plane1.hashCode(), plane2.hashCode())
    }

    @Test
    fun `test plane not equals with different distance`() {
        val normal1 = Vector3(1f, 0f, 0f)
        val plane1 = Plane(normal1, 2f)
        val normal2 = Vector3(1f, 0f, 0f)
        val plane2 = Plane(normal2, 3f)
        assertNotEquals(plane1, plane2)
        assertNotEquals(plane1.hashCode(), plane2.hashCode())
    }

    @Test
    fun `test plane not equals with different distance (float precision)`() {
        val normal1 = Vector3(1f, 0f, 0f)
        val plane1 = Plane(normal1, 2.0001f)
        val normal2 = Vector3(1f, 0f, 0f)
        val plane2 = Plane(normal2, 2f)
        assertNotEquals(plane1, plane2)
    }

    @Test
    fun `test plane equals with same object`() {
        val normal1 = Vector3(1f, 0f, 0f)
        val plane1 = Plane(normal1, 2f)

        assertEquals(plane1, plane1)
        assertEquals(plane1.hashCode(), plane1.hashCode())
    }
}