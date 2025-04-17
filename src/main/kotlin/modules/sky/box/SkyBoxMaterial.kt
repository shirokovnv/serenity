package modules.sky.box

import core.math.Matrix4
import graphics.assets.surface.BaseMaterial
import graphics.assets.texture.CubemapTexture

class SkyBoxMaterial : BaseMaterial<SkyBoxMaterial, SkyBoxShader>() {
    lateinit var cubemapTexture: CubemapTexture
    lateinit var worldViewProjection: Matrix4
}