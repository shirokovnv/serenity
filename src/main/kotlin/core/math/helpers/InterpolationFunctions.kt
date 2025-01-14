package core.math.helpers

fun lerp(a: Float, b: Float, t: Float): Float {
    return a + t * (b - a)
}