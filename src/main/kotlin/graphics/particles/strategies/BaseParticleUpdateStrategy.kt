package graphics.particles.strategies

import core.math.Vector3
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
}