package modules.terrain.quadtree.top_view

import core.math.Matrix4
import graphics.assets.surface.BaseMaterial

class QuadTreeTopViewMaterial: BaseMaterial<QuadTreeTopViewMaterial, QuadTreeTopViewShader>() {
    var model: Matrix4 = Matrix4().identity()
    lateinit var viewProj: Matrix4
}