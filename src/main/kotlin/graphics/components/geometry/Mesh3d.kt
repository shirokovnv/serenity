package graphics.components.geometry

import core.ecs.Component
import core.math.Vector2
import core.math.Vector3

open class Mesh3d : Component() {

    private var vertices = mutableListOf<Vector3>()
    private var normals = mutableListOf<Vector3>()
    private var uvs = mutableListOf<Vector2>()

    fun getVertices(): MutableList<Vector3> {
        return vertices.toMutableList()
    }

    fun getNormals(): MutableList<Vector3> {
        return normals.toMutableList()
    }

    fun getUVs(): MutableList<Vector2> {
        return uvs.toMutableList()
    }

    fun setVertices(vertices: List<Vector3>) {
        this.vertices = vertices.toMutableList()
    }

    fun setNormals(normals: List<Vector3>) {
        this.normals = normals.toMutableList()
    }

    fun setUVs(uvs: List<Vector2>) {
        this.uvs = uvs.toMutableList()
    }

    fun hasVertices(): Boolean = vertices.isNotEmpty()
    fun hasNormals(): Boolean = normals.isNotEmpty()
    fun hasUVs(): Boolean = uvs.isNotEmpty()
}