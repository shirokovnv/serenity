package modules.terrain.roam.tri.mesh

import modules.terrain.roam.tri.TriNode
import modules.terrain.roam.tri.TriNodeGeometry

abstract class TriMesh<out T>(protected val maxNumTriangles: Int) {
    protected val meshTris = HashMap<Int, TriNode>(maxNumTriangles)
    protected var meshIterator: Int = 0

    abstract val meshScheme: TriMeshScheme

    abstract fun getMeshData(): T
    fun count(): Int = meshIterator

    abstract fun addMeshData(geometry: TriNodeGeometry)
    abstract fun releaseMeshData(geometry: TriNodeGeometry)
}