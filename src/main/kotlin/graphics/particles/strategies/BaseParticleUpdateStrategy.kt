package graphics.particles.strategies

import core.math.Vector3
import graphics.particles.Particle
import graphics.particles.interfaces.ParticleUpdateStrategyInterface

abstract class BaseParticleUpdateStrategy : ParticleUpdateStrategyInterface {
    companion object {
        @JvmStatic
        protected val rotationCoefficient: Float = 0.01f

        @JvmStatic
        protected val zeroVector = Vector3(0f)

        @JvmStatic
        protected val zAxis = Vector3(0f, 0f, 1f)
    }

    protected fun updateBaseProperties(particle: Particle, deltaTime: Float) {
        particle.lifeRemaining -= deltaTime
        particle.position = particle.position + particle.velocity * deltaTime
        particle.rotation += rotationCoefficient * deltaTime
    }

    protected fun updateTransform(particle: Particle) {
        particle.transform.setRotation(zeroVector)
        particle.transform.rotateAroundAxis(particle.rotation, zAxis)
        particle.transform.setScale(Vector3(particle.scale, particle.scale, 1f))
        particle.transform.setTranslation(particle.position)
    }
}