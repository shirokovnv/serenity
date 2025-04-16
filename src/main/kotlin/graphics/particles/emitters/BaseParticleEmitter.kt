package graphics.particles.emitters

import core.math.Vector3
import graphics.particles.Particle
import graphics.particles.ParticleProps
import graphics.particles.interfaces.ParticleEmitterInterface
import kotlin.random.Random

abstract class BaseParticleEmitter : ParticleEmitterInterface {
    protected fun emitBaseProperties(particle: Particle, particleProps: ParticleProps) {
        // Position
        particle.position = Vector3(particleProps.position)

        // Color
        particle.colorBegin = particleProps.colorBegin
        particle.colorEnd = particleProps.colorEnd

        // Lifetime
        particle.lifeTime = particleProps.lifeTime
        particle.lifeRemaining = particleProps.lifeTime

        // Size with variation
        particle.sizeBegin = particleProps.sizeBegin + particleProps.sizeVariation * (Random.nextFloat() - 0.5f)
        particle.sizeEnd = particleProps.sizeEnd
    }
}