package graphics.particles

import core.math.Matrix4
import graphics.assets.surface.BaseMaterial
import graphics.assets.texture.Texture2d

class ParticleMaterial : BaseMaterial<ParticleMaterial, ParticleShader>() {
    lateinit var model: Matrix4
    lateinit var view: Matrix4
    lateinit var projection: Matrix4
    var texture: Texture2d? = null
    var textureNumRows: Int = 1
}