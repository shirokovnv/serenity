package core.math

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class Matrix4Test {

    @Test
    fun `test Zero`() {
        val matrix = Matrix4().zero()

        for (i in 0..3) {
            for (j in 0..3) {
                assertEquals(0f, matrix[i,j], 0.00001f)
            }
        }
    }

    @Test
    fun `test Identity`() {
        val matrix = Matrix4().identity()

        for (i in 0..3) {
            for (j in 0..3) {
                if(i == j){
                    assertEquals(1f, matrix[i,j], 0.00001f)
                }else{
                    assertEquals(0f, matrix[i,j], 0.00001f)
                }
            }
        }
    }

    @Test
    fun `test invert`() {
        val matrix = Matrix4()
        matrix[0, 0] = 2f
        matrix[0, 1] = 1f
        matrix[0, 2] = 0f
        matrix[0, 3] = 0f
        matrix[1, 0] = 1f
        matrix[1, 1] = 2f
        matrix[1, 2] = 1f
        matrix[1, 3] = 0f
        matrix[2, 0] = 0f
        matrix[2, 1] = 1f
        matrix[2, 2] = 2f
        matrix[2, 3] = 1f
        matrix[3, 0] = 0f
        matrix[3, 1] = 0f
        matrix[3, 2] = 1f
        matrix[3, 3] = 2f

        val inverted = matrix.invert()

        val expected = Matrix4()
        expected[0, 0] = 0.8f
        expected[0, 1] = -0.6f
        expected[0, 2] = 0.4f
        expected[0, 3] = -0.2f

        expected[1, 0] = -0.6f
        expected[1, 1] = 1.2f
        expected[1, 2] = -0.8f
        expected[1, 3] = 0.4f

        expected[2, 0] = 0.4f
        expected[2, 1] = -0.8f
        expected[2, 2] = 1.2f
        expected[2, 3] = -0.6f

        expected[3, 0] = -0.2f
        expected[3, 1] = 0.4f
        expected[3, 2] = -0.6f
        expected[3, 3] = 0.8f

        for (i in 0..3) {
            for (j in 0..3) {
                assertEquals(expected[i,j], inverted[i,j], 0.00001f)
            }
        }
    }

    @Test
    fun `test invert not invertible matrix`() {
        val matrix = Matrix4()
        assertFailsWith<IllegalStateException> {
            matrix.invert()
        }
    }

    @Test
    fun `test plus`() {
        val matrix1 = Matrix4()
        val matrix2 = Matrix4()

        for (i in 0..3) {
            for (j in 0..3) {
                matrix1[i, j] = (i * 4 + j).toFloat()
                matrix2[i, j] = (i * 4 + j + 1).toFloat()
            }
        }
        val result = matrix1 + matrix2
        for (i in 0..3) {
            for (j in 0..3) {
                assertEquals(matrix1[i,j] + matrix2[i,j], result[i,j])
            }
        }
    }

    @Test
    fun `test minus`() {
        val matrix1 = Matrix4()
        val matrix2 = Matrix4()

        for (i in 0..3) {
            for (j in 0..3) {
                matrix1[i, j] = (i * 4 + j).toFloat()
                matrix2[i, j] = (i * 4 + j + 1).toFloat()
            }
        }
        val result = matrix1 - matrix2
        for (i in 0..3) {
            for (j in 0..3) {
                assertEquals(matrix1[i,j] - matrix2[i,j], result[i,j])
            }
        }
    }

    @Test
    fun `test times matrix`() {
        val matrix1 = Matrix4()
        val matrix2 = Matrix4()

        matrix1[0, 0] = 1f
        matrix1[0, 1] = 2f
        matrix1[0, 2] = 3f
        matrix1[0, 3] = 4f
        matrix1[1, 0] = 5f
        matrix1[1, 1] = 6f
        matrix1[1, 2] = 7f
        matrix1[1, 3] = 8f
        matrix1[2, 0] = 9f
        matrix1[2, 1] = 10f
        matrix1[2, 2] = 11f
        matrix1[2, 3] = 12f
        matrix1[3, 0] = 13f
        matrix1[3, 1] = 14f
        matrix1[3, 2] = 15f
        matrix1[3, 3] = 16f

        matrix2[0, 0] = 1f
        matrix2[0, 1] = 2f
        matrix2[0, 2] = 3f
        matrix2[0, 3] = 4f
        matrix2[1, 0] = 5f
        matrix2[1, 1] = 6f
        matrix2[1, 2] = 7f
        matrix2[1, 3] = 8f
        matrix2[2, 0] = 9f
        matrix2[2, 1] = 10f
        matrix2[2, 2] = 11f
        matrix2[2, 3] = 12f
        matrix2[3, 0] = 13f
        matrix2[3, 1] = 14f
        matrix2[3, 2] = 15f
        matrix2[3, 3] = 16f


        val result = matrix1 * matrix2
        assertEquals(90f, result[0,0],0.00001f)
        assertEquals(100f, result[0,1],0.00001f)
        assertEquals(110f, result[0,2],0.00001f)
        assertEquals(120f, result[0,3],0.00001f)

        assertEquals(202f, result[1,0],0.00001f)
        assertEquals(228f, result[1,1],0.00001f)
        assertEquals(254f, result[1,2],0.00001f)
        assertEquals(280f, result[1,3],0.00001f)

        assertEquals(314f, result[2,0],0.00001f)
        assertEquals(356f, result[2,1],0.00001f)
        assertEquals(398f, result[2,2],0.00001f)
        assertEquals(440f, result[2,3],0.00001f)

        assertEquals(426f, result[3,0],0.00001f)
        assertEquals(484f, result[3,1],0.00001f)
        assertEquals(542f, result[3,2],0.00001f)
        assertEquals(600f, result[3,3],0.00001f)
    }

    @Test
    fun `test times quaternion`() {
        val matrix = Matrix4()
        val quaternion = Quaternion(1f, 2f, 3f, 4f)

        matrix[0, 0] = 1f
        matrix[0, 1] = 2f
        matrix[0, 2] = 3f
        matrix[0, 3] = 4f
        matrix[1, 0] = 5f
        matrix[1, 1] = 6f
        matrix[1, 2] = 7f
        matrix[1, 3] = 8f
        matrix[2, 0] = 9f
        matrix[2, 1] = 10f
        matrix[2, 2] = 11f
        matrix[2, 3] = 12f
        matrix[3, 0] = 13f
        matrix[3, 1] = 14f
        matrix[3, 2] = 15f
        matrix[3, 3] = 16f

        val result = matrix * quaternion
        assertEquals(30f, result.x,0.00001f)
        assertEquals(70f, result.y,0.00001f)
        assertEquals(110f, result.z,0.00001f)
        assertEquals(150f, result.w,0.00001f)
    }

    @Test
    fun `test set and get`() {
        val matrix = Matrix4()
        matrix[1, 2] = 10f
        assertEquals(10f, matrix[1,2])
    }

    @Test
    fun `test toString`() {
        val matrix = Matrix4()
        for (i in 0..3) {
            for (j in 0..3) {
                matrix[i, j] = (i * 4 + j).toFloat()
            }
        }
        val expected = """|0.0 1.0 2.0 3.0|
            |4.0 5.0 6.0 7.0|
            |8.0 9.0 10.0 11.0|
            |12.0 13.0 14.0 15.0|"""
        assertEquals(expected, matrix.toString())
    }

    @Test
    fun `test equals`() {
        val matrix1 = Matrix4()
        val matrix2 = Matrix4()

        for (i in 0..3) {
            for (j in 0..3) {
                matrix1[i, j] = (i * 4 + j).toFloat()
                matrix2[i, j] = (i * 4 + j).toFloat()
            }
        }

        val matrix3 = Matrix4()
        for (i in 0..3) {
            for (j in 0..3) {
                matrix3[i, j] = (i * 4 + j + 1).toFloat()
            }
        }

        assertEquals(matrix1, matrix2)
        assertNotEquals(matrix1, matrix3)
    }

    @Test
    fun `test hashCode`() {
        val matrix1 = Matrix4()
        val matrix2 = Matrix4()
        for (i in 0..3) {
            for (j in 0..3) {
                matrix1[i, j] = (i * 4 + j).toFloat()
                matrix2[i, j] = (i * 4 + j).toFloat()
            }
        }

        assertEquals(matrix1.hashCode(), matrix2.hashCode())
    }
}