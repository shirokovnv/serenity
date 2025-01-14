package core.math.helpers

fun fade(t: Float): Float {
    return t * t * t * (t * (t * 6 - 15) + 10)
}