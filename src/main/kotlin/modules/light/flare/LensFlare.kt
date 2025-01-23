package modules.light.flare

import core.management.Resources
import core.scene.Object
import graphics.assets.texture.Texture2d
import platform.services.filesystem.ImageLoader

class LensFlare: Object() {
    init {
        val imageLoader = Resources.get<ImageLoader>()!!
        val textures = mutableListOf<Texture2d>()
        val numFlares = 9
        for (i in 0..<numFlares) {
            val texture = Texture2d(imageLoader.loadImage("textures/flare/flare${i+1}.png"))
            texture.bind()
            texture.bilinearFilter()
            textures.add(texture)
        }

        val flareTextures = mutableListOf(
            LensFlareTexture(textures[5], 0.5f),
            LensFlareTexture(textures[3], 0.23f),
            LensFlareTexture(textures[1], 0.1f),
            LensFlareTexture(textures[6], 0.05f),
            LensFlareTexture(textures[0], 0.02f),
            LensFlareTexture(textures[2], 0.06f),
            LensFlareTexture(textures[8], 0.12f),
            LensFlareTexture(textures[4], 0.07f),
            LensFlareTexture(textures[0], 0.012f),
            LensFlareTexture(textures[6], 0.2f),
            LensFlareTexture(textures[8], 0.1f),
            LensFlareTexture(textures[2], 0.07f),
            LensFlareTexture(textures[4], 0.3f),
            LensFlareTexture(textures[3], 0.4f),
            LensFlareTexture(textures[7], 0.6f)
        )

        val flareMesh = LensFlareMesh()
        val flareBuffer = LensFlareBuffer(flareMesh)

        addComponent(LensFlareRenderer(flareTextures, flareBuffer, 0.76f))
    }
}