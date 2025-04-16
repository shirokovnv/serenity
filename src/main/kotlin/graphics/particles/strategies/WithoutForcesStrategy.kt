package graphics.particles.strategies

import graphics.particles.Particle

class WithoutForcesStrategy : BaseParticleUpdateStrategy() {
    override fun onUpdate(particle: Particle, deltaTime: Float) {
        updateBaseProperties(particle, deltaTime)
        updateTransform(particle)
    }
}