package modules.light.flare

import core.math.Quaternion
import graphics.assets.surface.BaseMaterial

class LensFlareMaterial: BaseMaterial<LensFlareMaterial, LensFlareShader>() {
    var transform: Quaternion = Quaternion(0f, 0f, 0f, 0f)
    var activeFlare: LensFlareTexture? = null
    var brightness: Float = 0.0f
}