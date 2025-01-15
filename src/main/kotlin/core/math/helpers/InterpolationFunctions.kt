package core.math.helpers

fun lerp(a: Float, b: Float, t: Float): Float {
    return a + t * (b - a)
}

fun smoothStep(edge0: Float, edge1: Float, x: Float): Float {
    val t = ((x - edge0) / (edge1 - edge0)).coerceIn(0f, 1f)
    return t * t * (3f - 2f * t)
}