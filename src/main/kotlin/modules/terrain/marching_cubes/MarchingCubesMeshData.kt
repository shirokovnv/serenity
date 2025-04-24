package modules.terrain.marching_cubes

import core.math.Vector3

class MarchingCubesMeshData(
    val vertices: MutableList<Vector3>,
    val normals: MutableList<Vector3>,
    val occlusions: MutableList<Float>
) {
    fun cleanUp() {
        vertices.clear()
        normals.clear()
        occlusions.clear()
    }
}