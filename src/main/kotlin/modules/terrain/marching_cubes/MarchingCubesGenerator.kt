package modules.terrain.marching_cubes

import core.math.Vector3
import core.scene.voxelization.VoxelGrid
import kotlin.math.abs

private const val fl = 0.00001f

class MarchingCubesGenerator(
    private val voxelGrid: VoxelGrid,
    private val isoLevel: Float
) {
    companion object {
        private const val EPSILON = 0.00001f
    }

    fun generateMesh(): MarchingCubesMeshData {
        val vertices = mutableListOf<Vector3>()

        for (x in 0..<voxelGrid.resolution - 1) {
            for (y in 0..<voxelGrid.resolution - 1) {
                for (z in 0..<voxelGrid.resolution - 1) {
                    marchCube(x, y, z, voxelGrid, vertices)
                }
            }
        }

        return MarchingCubesMeshData(vertices)
    }

    private fun marchCube(x: Int, y: Int, z: Int, voxelGrid: VoxelGrid, vertices: MutableList<Vector3>) {
        // Get the correct triangulation
        val tri = getTriangulation(x, y, z, voxelGrid)

        for (edgeIndex in tri) {
            if (edgeIndex < 0) break

            // Get edge
            val pointIndices = edgeTable[edgeIndex]

            // Get 2 points connecting this edge
            val p0 = pointsTable[pointIndices[0]]
            val p1 = pointsTable[pointIndices[1]]

            // Global position of these 2 points
            val posA = Vector3(x + p0.x, y + p0.y, z + p0.z)
            val posB = Vector3(x + p1.x, y + p1.y, z + p1.z)

            // Interpolate between these 2 points to get our mesh's vertex position
            val position = calculateInterpolation(posA, posB, voxelGrid)

            // Add our new vertex to our mesh's vertices array
            vertices.add(position)
        }
    }

    private fun getTriangulation(x: Int, y: Int, z: Int, voxelGrid: VoxelGrid): IntArray {
        var idx = 0

        idx = idx or ((if (isWithinBounds(x, y, z, voxelGrid) && voxelGrid.read(x, y, z) < isoLevel) 1 else 0) shl 0)
        idx = idx or ((if (isWithinBounds(x, y, z + 1, voxelGrid) && voxelGrid.read(
                x,
                y,
                z + 1
            ) < isoLevel
        ) 1 else 0) shl 1)
        idx = idx or ((if (isWithinBounds(x + 1, y, z + 1, voxelGrid) && voxelGrid.read(
                x + 1,
                y,
                z + 1
            ) < isoLevel
        ) 1 else 0) shl 2)
        idx = idx or ((if (isWithinBounds(x + 1, y, z, voxelGrid) && voxelGrid.read(
                x + 1,
                y,
                z
            ) < isoLevel
        ) 1 else 0) shl 3)
        idx = idx or ((if (isWithinBounds(x, y + 1, z, voxelGrid) && voxelGrid.read(
                x,
                y + 1,
                z
            ) < isoLevel
        ) 1 else 0) shl 4)
        idx = idx or ((if (isWithinBounds(x, y + 1, z + 1, voxelGrid) && voxelGrid.read(
                x,
                y + 1,
                z + 1
            ) < isoLevel
        ) 1 else 0) shl 5)
        idx = idx or ((if (isWithinBounds(x + 1, y + 1, z + 1, voxelGrid) && voxelGrid.read(
                x + 1,
                y + 1,
                z + 1
            ) < isoLevel
        ) 1 else 0) shl 6)
        idx = idx or ((if (isWithinBounds(x + 1, y + 1, z, voxelGrid) && voxelGrid.read(
                x + 1,
                y + 1,
                z
            ) < isoLevel
        ) 1 else 0) shl 7)

        return triTable[idx]
    }

    private fun isWithinBounds(x: Int, y: Int, z: Int, voxelGrid: VoxelGrid): Boolean {
        return x >= 0 && x < voxelGrid.resolution &&
                y >= 0 && y < voxelGrid.resolution &&
                z >= 0 && z < voxelGrid.resolution
    }

    private fun calculateInterpolation(a: Vector3, b: Vector3, voxelGrid: VoxelGrid): Vector3 {
        // Get the values at point A and point B, checking if the read is valid.
        val valA = if (isWithinBounds(a.x.toInt(), a.y.toInt(), a.z.toInt(), voxelGrid)) {
            voxelGrid.read(a.x.toInt(), a.y.toInt(), a.z.toInt())
        } else {
            return a // Return "a" if out of bounds.  Consider a better fallback strategy.
        }

        val valB = if (isWithinBounds(b.x.toInt(), b.y.toInt(), b.z.toInt(), voxelGrid)) {
            voxelGrid.read(b.x.toInt(), b.y.toInt(), b.z.toInt())
        } else {
            return b // Return "b" if out of bounds. Consider a better fallback strategy.
        }

        if (abs(isoLevel - valA) < EPSILON) {
            return a
        }

        if (abs(isoLevel - valB) < EPSILON) {
            return b
        }

        if (abs(valA - valB) < EPSILON) {
            return a
        }

        val t = (isoLevel - valA) / (valB - valA)
        return Vector3(
            a.x + t * (b.x - a.x),
            a.y + t * (b.y - a.y),
            a.z + t * (b.z - a.z)
        )
    }

    private fun compareVectors(a: Vector3, b: Vector3): Boolean {
        if (a.x < b.x) {
            return true
        } else if (a.x > b.x){
            return false
        }

        if (a.y < b.y) {
            return true
        } else if (a.y > b.y){
            return false
        }

        if (a.z < b.z) {
            return true
        } else if (a.z > b.z){
            return false
        }

        return false
    }
}