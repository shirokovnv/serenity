package modules.terrain.roam.gizmos

import core.math.Matrix4
import graphics.assets.surface.BaseMaterial
import graphics.rendering.Color
import modules.terrain.heightmap.Heightmap

class RoamPatchBoundsMaterial: BaseMaterial<RoamPatchBoundsMaterial, RoamPatchBoundsShader>() {
    lateinit var model: Matrix4
    lateinit var world: Matrix4
    lateinit var viewProj: Matrix4
    lateinit var color: Color
    lateinit var heightmap: Heightmap
}