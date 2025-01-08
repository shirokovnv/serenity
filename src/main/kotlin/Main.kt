import core.ecs.Behaviour
import core.math.Rect3d
import core.math.Vector3
import core.scene.Object
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
        val scene = SceneGraph(
            Rect3d(
                Vector3(0f),
                Vector3(1000f)
            )
        )

        class DebugBehaviour : Behaviour() {
            override fun create() {
                TODO("Not yet implemented")
            }

            override fun update(deltaTime: Float) {
                //println("Updated $deltaTime")
            }

            override fun destroy() {
                TODO("Not yet implemented")
            }

        }

        val debugObj = Object()
        val debugBehaviour = DebugBehaviour()
        debugObj.addComponent(debugBehaviour)

        scene.attachToRoot(debugObj)

        return scene
    }
}

fun main() {
    val app = App(settings)

    app.launch()
}