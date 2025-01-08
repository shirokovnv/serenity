import core.math.Rect3d
import core.math.Vector3
import core.scene.SceneGraph
import platform.Application
import platform.ApplicationSettings

val settings = ApplicationSettings(
    1280,
    720,
    200.0f,
    "Serenity Engine - OPENGL"
)

class App(private val settings: ApplicationSettings): Application(settings) {
    override fun oneTimeSceneInit(): SceneGraph {
        return SceneGraph(
            Rect3d(
                Vector3(0f),
                Vector3(1000f)
            )
        )
    }
}

fun main() {
    val app = App(settings)

    app.launch()
}