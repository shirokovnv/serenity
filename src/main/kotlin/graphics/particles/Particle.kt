package graphics.particles

import core.math.Quaternion
import core.math.Vector3
import core.math.helpers.lerp
import core.scene.Transform

class Particle {
    var position: Vector3 = Vector3()
    var velocity: Vector3 = Vector3()
    var colorBegin: Quaternion = Quaternion()
    var colorEnd: Quaternion = Quaternion()
    var rotation: Float = 0.0f
    var sizeBegin: Float = 0.0f
    var sizeEnd: Float = 0.0f
    var lifeTime: Float = 1.0f
    var lifeRemaining: Float = 0.0f
    var active: Boolean = false

    val life: Float
        get() = lifeRemaining / lifeTime

    val scale: Float
        get() = lerp(sizeBegin, sizeEnd, life)

    val color: Quaternion
        get() = lerp(colorBegin, colorEnd, life)

    val transform: Transform = Transform()
}