package modules.terrain.roam.tri.mesh

import modules.terrain.roam.tri.TriNodeGeometry

class TriVertexMesh(maxNumTriangles: Int) : TriMesh<FloatArray>(maxNumTriangles) {
    private val meshVertices = FloatArray(maxNumTriangles * 3 * 2)

    override val meshScheme: TriMeshScheme
        get() = TriMeshScheme.MESH_VERTICES

    override fun getMeshData(): FloatArray {
        return meshVertices.copyOfRange(0, meshIterator)
    }

    override fun addMeshData(geometry: TriNodeGeometry) {
        if (geometry.meshIndex != -1) {
            return
        }

        if (meshIterator >= meshVertices.size) {
            return
        }

        geometry.meshIndex = meshIterator

        meshVertices[meshIterator] = geometry.localVertices[0].x
        meshVertices[meshIterator + 1] = geometry.localVertices[0].y

        meshVertices[meshIterator + 2] = geometry.localVertices[1].x
        meshVertices[meshIterator + 3] = geometry.localVertices[1].y

        meshVertices[meshIterator + 4] = geometry.localVertices[2].x
        meshVertices[meshIterator + 5] = geometry.localVertices[2].y

        meshIterator += 6

        meshTris[geometry.meshIndex] = geometry.node
    }

    override fun releaseMeshData(geometry: TriNodeGeometry) {
        if (geometry.meshIndex == -1) {
            return
        }

        if (meshIterator <= 0) {
            return
        }

        val meshIndex = geometry.meshIndex

        // SWAP WITH LAST 6 ELEMENTS
        meshVertices[meshIndex] = meshVertices[meshIterator - 6]
        meshVertices[meshIndex + 1] = meshVertices[meshIterator - 5]

        meshVertices[meshIndex + 2] = meshVertices[meshIterator - 4]
        meshVertices[meshIndex + 3] = meshVertices[meshIterator - 3]

        meshVertices[meshIndex + 4] = meshVertices[meshIterator - 2]
        meshVertices[meshIndex + 5] = meshVertices[meshIterator - 1]

        meshTris.remove(meshIndex)
        val tri = meshTris.remove(meshIterator - 6)
        if (tri != null) {
            tri.geometry.meshIndex = meshIndex
            meshTris[meshIndex] = tri
        }

        geometry.meshIndex = -1
        meshIterator -= 6
    }
}