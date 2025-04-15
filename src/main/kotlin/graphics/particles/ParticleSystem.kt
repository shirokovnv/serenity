package graphics.particles

import core.management.structs.StaticArray
import graphics.particles.interfaces.ParticleEmitterInterface
import graphics.particles.interfaces.ParticleUpdateStrategyInterface

class ParticleSystem(
    private val emitter: ParticleEmitterInterface,
    private val updateStrategy: ParticleUpdateStrategyInterface
) {
    companion object {
        private const val MAX_PARTICLES_PER_SYSTEM = 1000
    }

    private var particlePool: Array<Particle> = Array(MAX_PARTICLES_PER_SYSTEM) { Particle() }
    private var activeParticles = StaticArray<Particle>(MAX_PARTICLES_PER_SYSTEM)
    private var poolIndex: Int = MAX_PARTICLES_PER_SYSTEM - 1

    fun activeParticles(): StaticArray<Particle> = activeParticles

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

            updateStrategy.onUpdate(particle, deltaTime)

            activeParticles.add(particle)
        }
    }

    fun emit(particleProps: ParticleProps) {
        // Wrap around logic for pool index
        poolIndex = (poolIndex + particlePool.size) % particlePool.size
        val particle = particlePool[poolIndex]

        particle.active = true

        emitter.onEmit(particle, particleProps)

        // Pool index increment
        poolIndex = (poolIndex + 1) % particlePool.size
    }
}