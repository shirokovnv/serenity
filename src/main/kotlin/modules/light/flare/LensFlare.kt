package modules.light.flare

import core.management.Resources
import core.scene.Object
import graphics.assets.texture.Texture2d
import platform.services.filesystem.ImageLoader

class LensFlare: Object() {
    init {
        val generator = LensFlareGenerator
        val flareTextures = generator.generateFlareTexturePack()
        val flareBuffer = generator.generateFlareBuffer()

        addComponent(LensFlareRenderBehaviour(flareTextures, flareBuffer, 0.76f))
    }
}