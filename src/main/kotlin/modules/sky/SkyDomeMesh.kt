package modules.sky

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

        val pitchAngle = 90.0f / numRows
        val headingAngle = 360.0f / numCols
        val apex = Vector3(0.0f, radius, 0.0f)

        var pitch = -90.0f
        for (heading in generateSequence(0.0f) { it + headingAngle }.takeWhile { it < 360.0f }) {
            vertices.add(Vector3(apex))

            val pos1 = initBySphericalCoordinates(radius, pitch + pitchAngle, heading + headingAngle)
            vertices.add(pos1)

            val pos2 = initBySphericalCoordinates(radius, pitch + pitchAngle, heading)
            vertices.add(pos2)
        }

        pitch = -90.0f + pitchAngle
        while (pitch < 0) {
            for (heading in generateSequence(0.0f) { it + headingAngle }.takeWhile { it < 360.0f }) {

                val pos0 = initBySphericalCoordinates(radius, pitch, heading)
                val pos1 = initBySphericalCoordinates(radius, pitch, heading + headingAngle)
                val pos2 = initBySphericalCoordinates(radius, pitch + pitchAngle, heading)
                val pos3 = initBySphericalCoordinates(radius, pitch + pitchAngle, heading + headingAngle)

                vertices.addAll(arrayOf(pos0, pos1, pos2, pos1, pos3, pos2))

            }
            pitch += pitchAngle
        }

        val uvs = mutableListOf<Vector2>()
        vertices.forEach { vertex ->
            val pn = Vector3(vertex).normalize()
            val uv = Vector2(
                asin(pn.x) / PI.toFloat() + 0.5f,
                asin(pn.y) / PI.toFloat() + 0.5f
            )

            uvs.add(uv)
        }

        setVertices(vertices)
        setUVs(uvs)
    }

    private fun initBySphericalCoordinates(radius: Float, pitch: Float, heading: Float): Vector3 {
        val x = radius * cos(pitch.toRadians()) * sin(heading.toRadians())
        val y = -radius * sin(pitch.toRadians())
        val z = radius * cos(pitch.toRadians()) * cos(heading.toRadians())

        return Vector3(x, y, z)
    }
}