package graphics.particles

import core.math.Quaternion
import core.math.Vector3

data class ParticleProps(
    val position: Vector3 = Vector3(),
    val velocity: Vector3 = Vector3(),
    val velocityVariation: Vector3 = Vector3(),
    val colorBegin: Quaternion = Quaternion(),
    val colorEnd: Quaternion = Quaternion(),
    val sizeBegin: Float = 0.0f,
    val sizeEnd: Float = 0.0f,
    val sizeVariation: Float = 0.0f,
    val lifeTime: Float = 1.0f
)