package core.math.extensions

import kotlin.math.max
import kotlin.math.min

fun Int.clamp(min: Int, max: Int): Int {
    return max(min, min(this, max))
}