package core.math

data class Triangle(val v0: Vector3, val v1: Vector3, val v2: Vector3) {
    val center: Vector3
        get() = (v0 + v1 + v2) / 3.0f
}