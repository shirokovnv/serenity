package core.scene

import core.math.Matrix4
import core.math.Vector3
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame

class TransformTest {

    @Test
    fun `test default transform values`() {
        val transform = Transform()

        assertEquals(Vector3(0f, 0f, 0f), transform.translation())
        assertEquals(Vector3(0f, 0f, 0f), transform.rotation())
        assertEquals(Vector3(1f, 1f, 1f), transform.scale())
        assertEquals(false, transform.isDirty())
        assertEquals(Matrix4().identity(), transform.matrix())
    }

    @Test
    fun `test set translation`() {
        val transform = Transform()
        val newTranslation = Vector3(1f, 2f, 3f)
        transform.setTranslation(newTranslation)
        assertEquals(newTranslation, transform.translation())
        assertEquals(true, transform.isDirty())
    }

    @Test
    fun `test set rotation`() {
        val transform = Transform()
        val newRotation = Vector3(45f, 30f, 60f)
        transform.setRotation(newRotation)
        assertEquals(newRotation, transform.rotation())
        assertEquals(true, transform.isDirty())

    }
    @Test
    fun `test set scale`() {
        val transform = Transform()
        val newScale = Vector3(2f, 3f, 4f)
        transform.setScale(newScale)
        assertEquals(newScale, transform.scale())
        assertEquals(true, transform.isDirty())
    }
    @Test
    fun `test matrix is updated when dirty`() {
        val transform = Transform()
        val initialMatrix = transform.matrix()

        transform.setTranslation(Vector3(1f, 0f, 0f))
        val updatedMatrix = transform.matrix()

        assertNotEquals(initialMatrix, updatedMatrix)
        assertEquals(false, transform.isDirty())
    }
    @Test
    fun `test combine matrix with parent matrix`() {
        val transform = Transform()
        val parentMatrix = Matrix4().identity()
        parentMatrix[0, 0] = 3f
        val initialMatrix = transform.matrix()
        transform.combineMatrixWith(parentMatrix)
        assertNotEquals(initialMatrix, transform.matrix())
        assertEquals(false, transform.isDirty())
    }
    @Test
    fun `test matrix updates correctly with rotation, translation, scale`() {
        val transform = Transform()
        val translation = Vector3(1f, 2f, 3f)
        val rotation = Vector3(45f, 0f, 0f)
        val scale = Vector3(2f, 2f, 2f)
        transform.setTranslation(translation)
        transform.setRotation(rotation)
        transform.setScale(scale)
        val result = transform.matrix();

        val expected = Matrix4().apply {
            m[0][0] = 2f
            m[0][1] = 0f
            m[0][2] = 0f
            m[0][3] = 1f

            m[1][0] = 0f
            m[1][1] = 1.0506439f
            m[1][2] = 1.7018071f
            m[1][3] = 2f

            m[2][0] = 0f
            m[2][1] = -1.7018071f
            m[2][2] = 1.0506439f
            m[2][3] = 3f

            m[3][0] = 0f
            m[3][1] = 0f
            m[3][2] = 0f
            m[3][3] = 1f
        }
        assertEquals(expected, result)
    }
    @Test
    fun `test matrix returns a copy`() {
        val transform = Transform()
        val initialMatrix = transform.matrix()
        initialMatrix[0,0] = 1f;
        val newMatrix = transform.matrix()
        assertNotSame(initialMatrix, newMatrix)
    }
}