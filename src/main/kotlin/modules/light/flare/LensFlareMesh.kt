package modules.light.flare

import core.math.Vector2
import graphics.geometry.Mesh2d

class LensFlareMesh: Mesh2d() {
    init {
        val vertices = listOf(
            Vector2(-0.5f, 0.5f),
            Vector2(-0.5f, -0.5f),
            Vector2(0.5f, 0.5f),
            Vector2(0.5f, -0.5f)
        )

        setVertices(vertices)
    }
}