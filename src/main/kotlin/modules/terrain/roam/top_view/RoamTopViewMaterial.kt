package modules.terrain.roam.top_view

import core.math.Matrix4
import graphics.assets.surface.BaseMaterial

class RoamTopViewMaterial : BaseMaterial<RoamTopViewMaterial, RoamTopViewShader>() {
    var model: Matrix4 = Matrix4().identity()
}