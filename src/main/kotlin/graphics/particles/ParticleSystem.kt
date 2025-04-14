package graphics.particles

import core.management.structs.StaticArray
import core.math.Vector3
import kotlin.math.PI
import kotlin.random.Random

class ParticleSystem {
    companion object {
        private const val MAX_PARTICLES_PER_SYSTEM = 1000
        private const val ROTATION_COEFFICIENT: Float = 0.01f
        private val zeroVector = Vector3(0f)
        private val zAxis = Vector3(0f, 0f, 1f)
    }

    private var particlePool: Array<Particle> = Array(MAX_PARTICLES_PER_SYSTEM) { Particle() }
    private var activeParticles = StaticArray<Particle>(MAX_PARTICLES_PER_SYSTEM)
    private var poolIndex: Int = MAX_PARTICLES_PER_SYSTEM - 1

    fun activeParticles(): Iterable<Particle> = activeParticles

    fun onUpdate(deltaTime: Float) {
        activeParticles.clear()

        for (particle in particlePool) {
            if (!particle.active) {
                continue
            }

            if (particle.lifeRemaining <= 0.0f) {
                particle.active = false
                continue
            }

            particle.lifeRemaining -= deltaTime
            particle.position = particle.position + particle.velocity * deltaTime
            particle.rotation += ROTATION_COEFFICIENT * deltaTime

            particle.transform.setRotation(zeroVector)
            particle.transform.rotateAroundAxis(particle.rotation, zAxis)
            particle.transform.setScale(Vector3(particle.scale, particle.scale, 1f))
            particle.transform.setTranslation(particle.position)

            activeParticles.add(particle)
        }
    }

    fun emit(particleProps: ParticleProps) {
        // Wrap around logic for pool index
        poolIndex = (poolIndex + particlePool.size) % particlePool.size
        val particle = particlePool[poolIndex]

        particle.active = true
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

        // Pool index increment
        poolIndex = (poolIndex + 1) % particlePool.size
    }
}