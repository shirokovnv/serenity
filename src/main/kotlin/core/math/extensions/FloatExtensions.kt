package core.math.extensions

import kotlin.math.floor

fun Float.toRadians(): Float {
    return Math.toRadians(this.toDouble()).toFloat()
}

fun Float.toIntFloor() = floor(this).toInt()