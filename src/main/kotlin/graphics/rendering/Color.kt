package graphics.rendering

import core.math.Vector3
import kotlin.math.abs

data class Color(val red: Float, val green: Float, val blue: Float, val alpha: Float = 1.0f) {
    fun toVector3(): Vector3 = Vector3(red, green, blue)
}

object Colors {
    val Black = Color(0.0f, 0.0f, 0.0f)
    val White = Color(1.0f, 1.0f, 1.0f)
    val Red = Color(1.0f, 0.0f, 0.0f)
    val Green = Color(0.0f, 1.0f, 0.0f)
    val Blue = Color(0.0f, 0.0f, 1.0f)
    val Yellow = Color(1.0f, 1.0f, 0.0f)
    val Magenta = Color(1.0f, 0.0f, 1.0f)
    val Cyan = Color(0.0f, 1.0f, 1.0f)
    val Orange = Color(0.8f, 0.5f, 0.1f)
    val LightGreen = Color(0.5f, 0.8f, 0.1f)
    val CornflowerBlue = Color(0.61f, 0.86f, 0.92f)
    val LightBlue = Color(0.68f, 0.85f, 0.90f)
    val LightRed = Color(1.0f, 0.5f, 0.5f)
}

object ColorGenerator {
    private var palette = arrayOf(
        Colors.Red,
        Colors.Blue,
        Colors.Green,
        Colors.Yellow,
        Colors.Magenta,
        Colors.Cyan,
        Colors.Orange,
        Colors.LightGreen
    )

    fun fromUUID(uuid: String): Color {
        val hashCode = uuid.hashCode()
        val index = abs(hashCode) % palette.size
        return palette[index]
    }
}