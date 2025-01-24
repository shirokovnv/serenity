package graphics.rendering.postproc.identity

import graphics.assets.surface.BaseMaterial
import graphics.assets.texture.Texture2d

class IdentityPPMaterial: BaseMaterial<IdentityPPMaterial, IdentityPPShader>() {
    lateinit var colorTexture: Texture2d
}