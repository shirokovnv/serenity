package core.math.extensions

import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

fun Float.toRadians(): Float {
    return Math.toRadians(this.toDouble()).toFloat()
}

fun Float.toDegrees(): Float {
    return Math.toDegrees(this.toDouble()).toFloat()
}

fun Float.toIntFloor() = floor(this).toInt()

fun Float.clamp(min: Float, max: Float): Float {
    return max(min, min(this, max))
}

fun Float.saturate(): Float {
    return this.clamp(0.0f, 1.0f)
}