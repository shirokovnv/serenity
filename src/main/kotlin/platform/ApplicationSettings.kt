package platform

import graphics.rendering.Color
import graphics.rendering.Colors

data class ApplicationSettings(
    val screenWidth: Int,
    val screenHeight: Int,
    val frameRate: Float,
    val title: String,
    val backgroundColor: Color = Colors.Black
    )