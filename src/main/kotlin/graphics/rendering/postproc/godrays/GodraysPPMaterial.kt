package graphics.rendering.postproc.godrays

import core.math.Vector2
import graphics.assets.surface.BaseMaterial
import graphics.assets.texture.Texture2d

class GodraysPPMaterial : BaseMaterial<GodraysPPMaterial, GodraysPPShader>() {
    var exposure: Float = 0.0034f
    var decay: Float = 1.0f
    var density: Float = 0.84f
    var weight: Float = 5.65f
    var lightScreenPosition: Vector2? = null

    lateinit var firstPass: Texture2d
    lateinit var secondPass: Texture2d
}