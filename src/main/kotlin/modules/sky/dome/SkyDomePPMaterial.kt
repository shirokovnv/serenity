package modules.sky.dome

import core.math.Matrix4
import graphics.assets.surface.BaseMaterial

class SkyDomePPMaterial: BaseMaterial<SkyDomePPMaterial, SkyDomePPShader>() {
    lateinit var worldViewProjection: Matrix4
}