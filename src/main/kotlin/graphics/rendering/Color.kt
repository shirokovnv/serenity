package graphics.rendering

data class Color(val red: Float, val green: Float, val blue: Float, val alpha: Float = 1.0f)

object Colors {
    val Black = Color(0.0f, 0.0f, 0.0f)
    val White = Color(1.0f, 1.0f, 1.0f)
    val CornflowerBlue = Color(0.61f, 0.86f, 0.92f)
}