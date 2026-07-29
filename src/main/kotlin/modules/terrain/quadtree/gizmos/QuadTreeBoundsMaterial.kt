package modules.terrain.quadtree.gizmos

import core.math.Matrix4
import graphics.assets.surface.BaseMaterial
import graphics.rendering.Color
import modules.terrain.heightmap.Heightmap

class QuadTreeBoundsMaterial: BaseMaterial<QuadTreeBoundsMaterial, QuadTreeBoundsShader>() {
    lateinit var viewProj: Matrix4
    lateinit var color: Color
}