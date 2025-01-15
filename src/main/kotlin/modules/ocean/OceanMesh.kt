package modules.ocean

import core.math.Vector2
import core.math.Vector3
import graphics.geometry.Mesh3d

class OceanMesh(private val gridSize: Int, private val uvScale: Float = 1.0f): Mesh3d() {
    init {
        val halfSize = 1.0f
        val step = 2.0f * halfSize / (gridSize - 1)

        val vertices = mutableListOf<Vector3>()
        val uvs = mutableListOf<Vector2>()
        val normals = mutableListOf<Vector3>()
        val indices = mutableListOf<Int>()

        for (y in 0..<gridSize) {
            for (x in 0..<gridSize) {
                val xPos = -halfSize + x * step
                val zPos = -halfSize + y * step
                val u = x.toFloat() / (gridSize - 1) * uvScale
                val v = y.toFloat() / (gridSize - 1) * uvScale

                vertices.add(Vector3(xPos, 0f, zPos))
                uvs.add(Vector2(u, v))
                normals.add(Vector3(0f, 1f, 0f))
            }
        }

        for (y in 0..<gridSize - 1) {
            for (x in 0..<gridSize - 1) {
                val a = y * gridSize + x
                val b = y * gridSize + x + 1
                val c = (y + 1) * gridSize + x
                val d = (y + 1) * gridSize + x + 1

                indices.addAll(listOf(a, c, b, b, c, d))
            }
        }

        setVertices(vertices)
        setUVs(uvs)
        setNormals(normals)
        setIndices(indices)
    }
}