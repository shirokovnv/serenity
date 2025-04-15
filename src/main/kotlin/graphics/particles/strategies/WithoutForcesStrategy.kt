package graphics.particles.strategies

import core.math.Vector3
import graphics.particles.Particle

class WithoutForcesStrategy : BaseParticleUpdateStrategy() {
    override fun onUpdate(particle: Particle, deltaTime: Float) {
        particle.lifeRemaining -= deltaTime
        particle.position = particle.position + particle.velocity * deltaTime
        particle.rotation += rotationCoefficient * deltaTime

        particle.transform.setRotation(zeroVector)
        particle.transform.rotateAroundAxis(particle.rotation, zAxis)
        particle.transform.setScale(Vector3(particle.scale, particle.scale, 1f))
        particle.transform.setTranslation(particle.position)
    }
}