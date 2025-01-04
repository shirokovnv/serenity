package core.math

data class Sphere(val center: Vector3, val radius: Float) : Shape {
    init {
        require(radius >= 0)
    }
}