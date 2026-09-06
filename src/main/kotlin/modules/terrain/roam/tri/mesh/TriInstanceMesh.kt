package modules.terrain.roam.tri.mesh

import modules.terrain.roam.tri.TriNodeGeometry

class TriInstanceMesh(maxNumTriangles: Int) : TriMesh<IntArray>(maxNumTriangles) {
    private val meshInstances = IntArray(maxNumTriangles)

    override val meshScheme: TriMeshScheme
        get() = TriMeshScheme.MESH_INSTANCES

    override fun getMeshData(): IntArray {
        return meshInstances.copyOfRange(0, meshIterator)
    }

    override fun addMeshData(geometry: TriNodeGeometry) {
        if (geometry.meshIndex != -1) {
            return
        }

        if (meshIterator >= meshInstances.size) {
            return
        }

        geometry.meshIndex = meshIterator
        meshInstances[meshIterator] = geometry.node.index * 3

        meshIterator++
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

        // SWAP WITH LAST
        meshInstances[meshIndex] = meshInstances[meshIterator - 1]

        meshTris.remove(meshIndex)
        val tri = meshTris.remove(meshIterator - 1)
        if (tri != null) {
            tri.geometry.meshIndex = meshIndex
            meshTris[meshIndex] = tri
        }

        geometry.meshIndex = -1
        meshIterator--
    }
}