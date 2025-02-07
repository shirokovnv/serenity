package core.math

data class Rect2d(val min: Vector2, val max: Vector2) : Shape {

    init {
        require(min.x <= max.x && min.y <= max.y)
    }

    val width: Float
        get() = max.x - min.x

    val height: Float
        get() = max.y - min.y

    fun size() = Vector2(max.x - min.x, max.y - min.y)

    val center: Vector2
        get() = Vector2((max.x + min.x) * 0.5f, (max.y + min.y) * 0.5f)
}