package modules.terrain.roam.tri.mesh

import modules.terrain.roam.tri.TriNodeGeometry

object TriMeshFactory {
    fun create(triMeshScheme: TriMeshScheme): TriMesh<Any> {
        return when (triMeshScheme) {
            TriMeshScheme.MESH_VERTICES -> TriVertexMesh(TriNodeGeometry.maxLeafTriangles)
            TriMeshScheme.MESH_INSTANCES -> TriInstanceMesh(TriNodeGeometry.maxLeafTriangles)
        }
    }
}