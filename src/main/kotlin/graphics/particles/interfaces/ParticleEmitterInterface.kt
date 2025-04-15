package graphics.particles.interfaces

import graphics.particles.Particle
import graphics.particles.ParticleProps

interface ParticleEmitterInterface {
    fun onEmit(particle: Particle, particleProps: ParticleProps)
}