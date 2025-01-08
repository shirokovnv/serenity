package graphics.geometry

import core.ecs.Component
import core.math.Vector2

open class Mesh2d : Component() {

    private var vertices = mutableListOf<Vector2>()
    private var uvs = mutableListOf<Vector2>()

    fun getVertices(): MutableList<Vector2> {
        return vertices.toMutableList()
    }

    fun getUVs(): MutableList<Vector2> {
        return uvs.toMutableList()
    }

    fun setVertices(vertices: List<Vector2>) {
        this.vertices = vertices.toMutableList()
    }

    fun setUVs(uvs: List<Vector2>) {
        this.uvs = uvs.toMutableList()
    }

    fun hasVertices(): Boolean = vertices.isNotEmpty()
    fun hasUVs(): Boolean = uvs.isNotEmpty()
}