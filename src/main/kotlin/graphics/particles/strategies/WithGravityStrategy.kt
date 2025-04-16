package graphics.particles.strategies

import graphics.particles.Particle

class WithGravityStrategy(
    private val gravity: Float
) : BaseParticleUpdateStrategy() {
    init {
        require(gravity >= 0.0f)
    }

    override fun onUpdate(particle: Particle, deltaTime: Float) {
        particle.velocity.y -= gravity * deltaTime

        updateBaseProperties(particle, deltaTime)
        updateTransform(particle)
    }
}