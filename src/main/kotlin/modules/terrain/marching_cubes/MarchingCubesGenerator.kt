package modules.terrain.marching_cubes

import core.math.Vector3
import core.math.extensions.saturate
import core.scene.voxelization.DensityProvider
import core.scene.voxelization.VoxelGrid
import java.util.stream.IntStream
import kotlin.math.*
import kotlin.random.Random

class MarchingCubesGenerator(
    private val voxelGrid: VoxelGrid,
    private val isoLevel: Float,
    private val densityProvider: DensityProvider
) {
    companion object {
        private const val EPSILON = 0.00001f

        private val raySphericalDistribution = Array(32) { i ->
            val rnd = Random(i)
            val u = rnd.nextFloat()
            val v = rnd.nextFloat()

            val theta = (2.0f * PI.toFloat() * u)
            val phi = acos((2.0f * v - 1.0f))

            val x = (sin(phi) * cos(theta))
            val y = (sin(phi) * sin(theta))
            val z = (cos(phi))

            Vector3(x, y, z).normalize()
        }
    }

    fun generateMesh(): MarchingCubesMeshData {
        val vertices = calculateVertices()
        val normalsAndOcclusions = calculateNormalsAndOcclusions(vertices)

        return MarchingCubesMeshData(
            vertices,
            normalsAndOcclusions.first,
            normalsAndOcclusions.second
        )
    }

    private fun calculateVertices(): MutableList<Vector3> {
        val vertices = mutableListOf<Vector3>()

        for (x in 0..<voxelGrid.resolution - 1) {
            for (y in 0..<voxelGrid.resolution - 1) {
                for (z in 0..<voxelGrid.resolution - 1) {
                    marchCube(x, y, z, voxelGrid, vertices)
                }
            }
        }

        return vertices
    }

    private fun calculateNormalsAndOcclusions(vertices: List<Vector3>): Pair<MutableList<Vector3>, MutableList<Float>> {
        val normals = mutableListOf<Vector3>()
        val occlusions = mutableListOf<Float>()

        // Pre-allocate lists with the correct size for better performance.
        // This avoids resizing during parallel processing.
        repeat(vertices.size) {
            normals.add(Vector3(0f, 0f, 0f)) // Initialize with a default value
            occlusions.add(0f) // Initialize with a default value
        }

        IntStream.range(0, vertices.size).parallel().forEach { i ->
            val position = vertices[i]
            val normal = calculateNormal(position, voxelGrid)
            val occlusion = calculateOcclusion(position)

            normals[i] = normal // Direct assignment is now thread-safe
            occlusions[i] = occlusion
        }

        return Pair(normals, occlusions)
    }

    private fun calculateNormal(position: Vector3, voxelGrid: VoxelGrid): Vector3 {
        val uvw = Vector3(
            position.x / voxelGrid.resolution.toFloat(),
            position.y / voxelGrid.resolution.toFloat(),
            position.z / voxelGrid.resolution.toFloat(),
        )

        val d = 1.0f / voxelGrid.resolution.toFloat()

        val grad = Vector3(
            sampleDensity(Vector3(uvw) + Vector3(d, 0f, 0f), voxelGrid) - sampleDensity(
                Vector3(uvw) + Vector3(
                    -d,
                    0f,
                    0f
                ), voxelGrid
            ),
            sampleDensity(Vector3(uvw) + Vector3(0f, d, 0f), voxelGrid) - sampleDensity(
                Vector3(uvw) + Vector3(
                    0f,
                    -d,
                    0f
                ), voxelGrid
            ),
            sampleDensity(Vector3(uvw) + Vector3(0f, 0f, d), voxelGrid) - sampleDensity(
                Vector3(uvw) + Vector3(
                    0f,
                    0f,
                    -d
                ), voxelGrid
            )
        )

        return grad.normalize() * -1f
    }

    private fun calculateOcclusion(ws: Vector3): Float {
        var visibility = 0f
        val bigStep = Vector3(voxelGrid.resolution.toFloat() / 4)
        for (rayIndex in 0..31) {
            val dir = raySphericalDistribution[rayIndex]

            var rayVisibility = 1f

            // Short-range samples from density volume:
            for (step in 1..16) {
                var position = (ws + dir * step.toFloat())
                position.x.coerceIn(0.0f, 100.0f)
                position.y.coerceIn(0.0f, 100.0f)
                position.z.coerceIn(0.0f, 100.0f)
                position = position / voxelGrid.resolution.toFloat()

                val d = sampleDensity(position, voxelGrid)
                rayVisibility *= (d * 9999f).saturate()
            }

            // Long-range samples from density function:
            for (step in 1..4) {
                var position = (ws + dir * bigStep * step.toFloat())
                position.x.coerceIn(0.0f, 100.0f)
                position.y.coerceIn(0.0f, 100.0f)
                position.z.coerceIn(0.0f, 100.0f)
                position = position / voxelGrid.resolution.toFloat()

                val d = densityProvider(position.x, position.y, position.z)
                rayVisibility *= (d * 9999f).saturate()
            }

            visibility += rayVisibility
        }

        return (1f - visibility / 32f)
    }

    private fun sampleDensity(uvw: Vector3, voxelGrid: VoxelGrid): Float {
        val x = uvw.x
        val y = uvw.y
        val z = uvw.z

        if (x < 0f || x > 1f || y < 0f || y > 1f || z < 0f || z > 1f) return 0f

        val sizeX = voxelGrid.resolution - 1
        val sizeY = voxelGrid.resolution - 1
        val sizeZ = voxelGrid.resolution - 1

        val fx = x * sizeX
        val fy = y * sizeY
        val fz = z * sizeZ

        val x0 = floor(fx).toInt().coerceIn(0, sizeX)
        val y0 = floor(fy).toInt().coerceIn(0, sizeY)
        val z0 = floor(fz).toInt().coerceIn(0, sizeZ)

        val x1 = ceil(fx).toInt().coerceIn(0, sizeX)
        val y1 = ceil(fy).toInt().coerceIn(0, sizeY)
        val z1 = ceil(fz).toInt().coerceIn(0, sizeZ)

        val rx = fx - x0
        val ry = fy - y0
        val rz = fz - z0

        val c000 = voxelGrid.read(x0, y0, z0)
        val c100 = voxelGrid.read(x1, y0, z0)
        val c010 = voxelGrid.read(x0, y1, z0)
        val c110 = voxelGrid.read(x1, y1, z0)
        val c001 = voxelGrid.read(x0, y0, z1)
        val c101 = voxelGrid.read(x1, y0, z1)
        val c011 = voxelGrid.read(x0, y1, z1)
        val c111 = voxelGrid.read(x1, y1, z1)

        val c00 = c000 * (1 - rx) + c100 * rx
        val c01 = c010 * (1 - rx) + c110 * rx
        val c10 = c001 * (1 - rx) + c101 * rx
        val c11 = c011 * (1 - rx) + c111 * rx

        val c0 = c00 * (1 - ry) + c01 * ry
        val c1 = c10 * (1 - ry) + c11 * ry

        return c0 * (1 - rz) + c1 * rz
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
}