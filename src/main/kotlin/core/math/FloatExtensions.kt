package core.math

fun Float.toRadians(): Float {
    return Math.toRadians(this.toDouble()).toFloat()
}