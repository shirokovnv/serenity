package modules.sky

import core.math.Matrix4
import core.math.Vector2
import graphics.assets.surface.BaseMaterial
import graphics.assets.texture.Texture2d

class SkyDomeMaterial: BaseMaterial<SkyDomeMaterial, SkyDomeShader>() {
    lateinit var cloudTexture: Texture2d
    lateinit var worldViewProjection: Matrix4
    lateinit var cloudAnimationOffset: Vector2
}