package graphics.particles.interfaces

import graphics.particles.Particle

interface ParticleUpdateStrategyInterface {
    fun onUpdate(particle: Particle, deltaTime: Float)
}