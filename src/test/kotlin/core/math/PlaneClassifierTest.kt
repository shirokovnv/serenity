package core.math

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class PlaneClassifierTest {

    @Test
    fun `test classify rect3d front`() {
        val plane = Plane.fromPoint(Vector3(0f,0f,0f), Vector3(0f, 1f, 0f))
        val rect3d = Rect3d(Vector3(0.1f, 0.1f, 0.1f), Vector3(0.2f, 0.2f, 0.2f))
        val result = PlaneClassifier.classifyWithRect3d(plane, rect3d)
        assertEquals(Plane.PlaneClassification.PLANE_FRONT, result)
    }

    @Test
    fun `test classify rect3d back`() {
        val plane = Plane.fromPoint(Vector3(0f,0f,0f), Vector3(0f, 1f, 0f))
        val rect3d = Rect3d(Vector3(-2f, -2f, -2f), Vector3(-1f, -1f, -1f))
        val result = PlaneClassifier.classifyWithRect3d(plane, rect3d)
        assertEquals(Plane.PlaneClassification.PLANE_BACK, result)
    }
    @Test
    fun `test classify rect3d intersect`() {
        val plane = Plane.fromPoint(Vector3(0f,0f,0f), Vector3(0f, 1f, 0f))
        val rect3d = Rect3d(Vector3(-1f, -1f, -1f), Vector3(1f, 1f, 1f))
        val result = PlaneClassifier.classifyWithRect3d(plane, rect3d)
        assertEquals(Plane.PlaneClassification.PLANE_INTERSECT, result)
    }
    @Test
    fun `test classify sphere front`() {
        val plane = Plane.fromPoint(Vector3(0f,0f,0f), Vector3(0f, 1f, 0f))
        val sphere = Sphere(Vector3(0.5f, 0.5f, 0.5f), 0.2f)
        val result = PlaneClassifier.classifyWithSphere(plane, sphere)
        assertEquals(Plane.PlaneClassification.PLANE_FRONT, result)
    }
    @Test
    fun `test classify sphere back`() {
        val plane = Plane.fromPoint(Vector3(0f,0f,0f), Vector3(0f, 1f, 0f))
        val sphere = Sphere(Vector3(-1f, -1f, -1f), 0.5f)
        val result = PlaneClassifier.classifyWithSphere(plane, sphere)
        assertEquals(Plane.PlaneClassification.PLANE_BACK, result)
    }

    @Test
    fun `test classify sphere intersect`() {
        val plane = Plane.fromPoint(Vector3(0f,0f,0f), Vector3(0f, 1f, 0f))
        val sphere = Sphere(Vector3(0f, 0f, 0f), 1f)
        val result = PlaneClassifier.classifyWithSphere(plane, sphere)
        assertEquals(Plane.PlaneClassification.PLANE_INTERSECT, result)
    }
}