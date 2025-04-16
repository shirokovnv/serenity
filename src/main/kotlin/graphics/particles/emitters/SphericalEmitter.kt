package graphics.particles.emitters

import core.math.Vector3
import graphics.particles.Particle
import graphics.particles.ParticleProps
import kotlin.math.PI
import kotlin.random.Random

class SphericalEmitter : BaseParticleEmitter() {
    override fun onEmit(particle: Particle, particleProps: ParticleProps) {
        emitBaseProperties(particle, particleProps)

        // Rotation
        particle.rotation = Random.nextFloat() * 2.0f * PI.toFloat()  // Random rotation

        // Velocity with variation
        particle.velocity = particleProps.velocity + Vector3(
            particleProps.velocityVariation.x * (Random.nextFloat() - 0.5f),
            particleProps.velocityVariation.y * (Random.nextFloat() - 0.5f),
            particleProps.velocityVariation.z * (Random.nextFloat() - 0.5f),
        )
    }
}