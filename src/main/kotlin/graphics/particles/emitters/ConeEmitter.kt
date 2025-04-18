package graphics.particles.emitters

import core.math.Quaternion
import core.math.Vector3
import core.math.createLookAtMatrix
import core.math.extensions.toRadians
import graphics.particles.Particle
import graphics.particles.ParticleProps
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class ConeEmitter(
    private val origin: Vector3,
    private val direction: Vector3,
    private val angle: Float
) : BaseParticleEmitter() {
    override fun onEmit(particle: Particle, particleProps: ParticleProps) {
        emitBaseProperties(particle, particleProps)

        particle.rotation = 0f

        // Velocity is a unit vector within cone
        particle.velocity = randomUnitVectorWithinCone(
            origin,
            direction,
            angle
        )
    }

    private fun randomUnitVectorWithinCone(origin: Vector3, direction: Vector3, angle: Float): Vector3 {
        val theta = (Random.nextDouble() * 360.0).toFloat()

        val cosPhi = cos(angle.toRadians())
        val z = Random.nextDouble(cosPhi.toDouble(), 1.0).toFloat()

        val sinPhi = sqrt(1 - z * z)
        val x = sinPhi * cos(theta.toRadians())
        val y = sinPhi * sin(theta.toRadians())

        val rotation = createLookAtMatrix(origin, direction + origin, Vector3(0f, 0f, 1f))

        return (rotation * Quaternion(x, y, z, 1.0f)).xyz().normalize()
    }
}