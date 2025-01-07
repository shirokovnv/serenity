import platform.Application
import platform.ApplicationSettings

val settings = ApplicationSettings(
    1280,
    720,
    200.0f,
    "Serenity Engine - OPENGL"
)

class App(private val settings: ApplicationSettings): Application(settings) {
    override fun oneTimeSceneInit() {

    }
}

fun main() {
    val app = App(settings)

    app.launch()
}