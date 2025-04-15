package graphics.particles.emitters

import core.math.Vector3
import graphics.particles.Particle
import graphics.particles.ParticleProps
import graphics.particles.interfaces.ParticleEmitterInterface
import kotlin.math.*
import kotlin.random.Random

class CylindricalEmitter(
    private val baseCenter: Vector3,
    private val topCenter: Vector3,
    private val radius: Float
) : ParticleEmitterInterface {
    override fun onEmit(particle: Particle, particleProps: ParticleProps) {
        particle.position = Vector3(particleProps.position) + randomPointInCylinderBase(baseCenter, topCenter, radius)
        particle.rotation = 0f

        // Velocity
        particle.velocity = cylinderBaseNormal(baseCenter, topCenter)

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

    private fun cylinderBaseNormal(baseCenter: Vector3, topCenter: Vector3): Vector3 {
        return (topCenter - baseCenter).normalize()
    }

    private fun randomPointInCircle2D(radius: Float): Vector3 {
        val r = radius * sqrt(Random.nextFloat())
        val theta = Random.nextFloat() * 2.0f * PI

        val x = r * cos(theta).toFloat()
        val y = r * sin(theta).toFloat()

        return Vector3(x, y, 0f)
    }

    private fun randomPointInCylinderBase(baseCenter: Vector3, topCenter: Vector3, radius: Float): Vector3 {
        val normal = cylinderBaseNormal(baseCenter, topCenter)

        val randomPoint2D = randomPointInCircle2D(radius)

        val w = normal.normalize()
        val u = if (abs(w.x) > abs(w.z)) Vector3(-w.y, w.x, 0f).normalize() else Vector3(0f, -w.z, w.y).normalize()
        val v = w.cross(u)

        return baseCenter + u * randomPoint2D.x + v * randomPoint2D.y
    }
}