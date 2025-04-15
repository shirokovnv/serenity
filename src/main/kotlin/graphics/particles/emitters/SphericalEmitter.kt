package graphics.particles.emitters

import core.math.Vector3
import graphics.particles.Particle
import graphics.particles.ParticleProps
import graphics.particles.interfaces.ParticleEmitterInterface
import kotlin.math.PI
import kotlin.random.Random

class SphericalEmitter : ParticleEmitterInterface {
    override fun onEmit(particle: Particle, particleProps: ParticleProps) {
        particle.position = Vector3(particleProps.position)
        particle.rotation = Random.nextFloat() * 2.0f * PI.toFloat()  // Random rotation

        // Velocity with variation
        particle.velocity = particleProps.velocity + Vector3(
            particleProps.velocityVariation.x * (Random.nextFloat() - 0.5f),
            particleProps.velocityVariation.y * (Random.nextFloat() - 0.5f),
            particleProps.velocityVariation.z * (Random.nextFloat() - 0.5f),
        )

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