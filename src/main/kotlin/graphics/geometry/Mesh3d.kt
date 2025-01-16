package graphics.geometry

import core.ecs.BaseComponent
import core.math.Vector2
import core.math.Vector3

open class Mesh3d : BaseComponent() {

    private var vertices = mutableListOf<Vector3>()
    private var normals = mutableListOf<Vector3>()
    private var uvs = mutableListOf<Vector2>()
    private var indices = mutableListOf<Int>()

    fun getVertices(): MutableList<Vector3> {
        return vertices.toMutableList()
    }

    fun getNormals(): MutableList<Vector3> {
        return normals.toMutableList()
    }

    fun getUVs(): MutableList<Vector2> {
        return uvs.toMutableList()
    }

    fun getIndices(): MutableList<Int> {
        return indices.toMutableList()
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

    fun setIndices(indices: List<Int>) {
        this.indices = indices.toMutableList()
    }

    fun hasVertices(): Boolean = vertices.isNotEmpty()
    fun hasNormals(): Boolean = normals.isNotEmpty()
    fun hasUVs(): Boolean = uvs.isNotEmpty()
    fun hasIndices(): Boolean = indices.isNotEmpty()

    fun countVertices(): Int = vertices.size
    fun countNormals(): Int = normals.size
    fun countUVs(): Int = uvs.size
    fun countIndices(): Int = indices.size
}