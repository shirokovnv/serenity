package modules.sky.dome

import core.math.Vector2
import core.math.Vector3
import core.math.extensions.toRadians
import graphics.geometry.Mesh3d
import kotlin.math.*

class SkyDomeMesh(
    private val numRows: Int,
    private val numCols: Int,
    private val radius: Float
) : Mesh3d() {

    init {
        val vertices = mutableListOf<Vector3>()
        val uvs = mutableListOf<Vector2>()

        val pitchAngle = 90.0f / numRows
        val headingAngle = 360.0f / numCols

        for (i in 0..<numRows) {
            val latitude = (i * pitchAngle).toRadians()

            for (j in 0..<numCols) {
                val heading = (j * headingAngle).toRadians()

                val x = radius * cos(latitude) * sin(heading)
                val y = radius * sin(latitude)
                val z = radius * cos(latitude) * cos(heading)

                val pos0 = Vector3(x, y, z)
                val headingNext = ((j + 1) * headingAngle).toRadians()

                val xNext = radius * cos(latitude) * sin(headingNext)
                val yNext = radius * sin(latitude)
                val zNext = radius * cos(latitude) * cos(headingNext)

                val pos1 = Vector3(xNext, yNext, zNext)

                val latitudeNext = ((i + 1) * pitchAngle).toRadians()
                val xNextLat = radius * cos(latitudeNext) * sin(heading)
                val yNextLat = radius * sin(latitudeNext)
                val zNextLat = radius * cos(latitudeNext) * cos(heading)

                val pos2 = Vector3(xNextLat, yNextLat, zNextLat)

                val xNextLatHeading = radius * cos(latitudeNext) * sin(headingNext)
                val yNextLatHeading = radius * sin(latitudeNext)
                val zNextLatHeading = radius * cos(latitudeNext) * cos(headingNext)

                val pos3 = Vector3(xNextLatHeading, yNextLatHeading, zNextLatHeading)

                val uv0 = calculateUV(j, i, numRows)
                val uv1 = calculateUV(j+1, i, numRows)
                val uv2 = calculateUV(j, i+1, numRows)
                val uv3 = calculateUV(j+1, i+1, numRows)

                vertices.addAll(listOf(pos0, pos1, pos2, pos1, pos3, pos2))
                uvs.addAll(listOf(uv0, uv1, uv2, uv1, uv3, uv2))
            }
        }

        setVertices(vertices)
        setUVs(uvs)
    }

    private fun calculateUV(j: Int, i: Int, numRows: Int): Vector2 {
        val u = (j.toFloat() / numCols)
        val v = (i.toFloat() / numRows) * 0.5f + 0.5f

        return Vector2(u, v)
    }
}