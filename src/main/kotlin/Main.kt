import core.ecs.Behaviour
import core.math.Rect3d
import core.math.Sphere
import core.math.Vector3
import core.scene.BoxAABB
import core.scene.Object
import core.scene.SceneGraph
import core.scene.Transform
import core.scene.camera.Frustum
import core.scene.camera.PerspectiveCamera
import core.scene.spatial.LinearQuadTree
import core.scene.spatial.SpatialHashGrid
import platform.Application
import platform.ApplicationSettings

val settings = ApplicationSettings(
    1280,
    720,
    200.0f,
    "Serenity Engine - OPENGL"
)

class App(settings: ApplicationSettings): Application(settings) {
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
//                println("Updated $deltaTime")
            }

            override fun destroy() {
                TODO("Not yet implemented")
            }

        }

        val debugObj = Object()
        val debugBehaviour = DebugBehaviour()
        debugObj.addComponent(debugBehaviour)

        scene.attachToRoot(debugObj)

        val camera = PerspectiveCamera(1280f, 720f, 70f, 0.1f, 1000f)
        //debugObj.getComponent<Transform>()!!.setScale(Vector3(30f))
        //debugObj.getComponent<Transform>()!!.setRotation(Vector3(90.0f.toRadians(), 180f.toRadians(), 0.0f))
        debugObj.addComponent(camera)
        debugObj.getComponent<Transform>()!!.setTranslation(Vector3(1f))
        debugObj.getComponent<BoxAABB>()!!.setShape(
            Rect3d(Vector3(1f), Vector3(3f))
        )

        //camera.transform.setTranslation(Vector3(10f))

        val frustum = Frustum(camera)

        println(frustum.searchVolume().shape())

        val quadTree = LinearQuadTree()
        quadTree.create(Rect3d(Vector3(0f, 0f, 0f), Vector3(10f, 10f, 10f)), 9)
        println(quadTree.insert(debugObj))

        val searchVolume = BoxAABB(Rect3d(
            Vector3(3.1f, 3.1f, 3.1f),
            Vector3(10f, 10f, 10f)
        ))
        println(quadTree.buildSearchResults(searchVolume).size)
        println(quadTree.countObjects())

        val spGrid = SpatialHashGrid(Rect3d(Vector3(0f), Vector3(1000f)), Vector3(64f, 64f, 32f))
        spGrid.insert(debugObj)

        println(spGrid.buildSearchResults(searchVolume))

        return scene
    }
}

fun main() {
    val app = App(settings)

    app.launch()
}