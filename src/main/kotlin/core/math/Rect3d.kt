package core.math

data class Rect3d(val min: Vector3, val max: Vector3): Shape {

    init {
        require(min.x <= max.x && min.y <= max.y && min.z <= max.z)
    }

    val width: Float
        get() = max.x - min.x

    val height: Float
        get() = max.y - min.y

    val depth: Float
        get() = max.z - min.z

    val center: Vector3
        get() = Vector3(
            (max.x + min.x) * 0.5f,
            (max.y + min.y) * 0.5f,
            (max.z + min.z) * 0.5f
            )

    val corners: List<Vector3>
        get() {
            return listOf(
                Vector3(min.x, min.y, min.z),
                Vector3(max.x, min.y, min.z),
                Vector3(min.x, max.y, min.z),
                Vector3(max.x, max.y, min.z),
                Vector3(min.x, min.y, max.z),
                Vector3(max.x, min.y, max.z),
                Vector3(min.x, max.y, max.z),
                Vector3(max.x, max.y, max.z)
            )
        }
}