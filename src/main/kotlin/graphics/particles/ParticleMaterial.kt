package graphics.particles

import core.math.Matrix4
import graphics.assets.surface.BaseMaterial

class ParticleMaterial : BaseMaterial<ParticleMaterial, ParticleShader>() {
    lateinit var model: Matrix4
    lateinit var view: Matrix4
    lateinit var projection: Matrix4
}