import core.ecs.Behaviour
import core.math.Rect3d
import core.math.Vector3
import core.scene.BoxAABB
import core.scene.Object
import core.scene.SceneGraph
import core.scene.Transform
import core.scene.camera.Camera
import core.scene.camera.CameraController
import core.scene.camera.Frustum
import core.scene.camera.PerspectiveCamera
import core.scene.spatial.LinearQuadTree
import core.scene.spatial.SpatialHashGrid
import graphics.assets.texture.Texture2d
import modules.input.InputController
import modules.terrain.Heightmap
import modules.terrain.tiled.TiledTerrain
import modules.terrain.tiled.TiledTerrainConfig
import platform.Application
import platform.ApplicationSettings
import platform.services.filesystem.ImageLoader

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
                println("Debug behaviour created")
            }

            override fun update(deltaTime: Float) {
//                println("Updated $deltaTime")
            }

            override fun destroy() {
                println("Debug behaviour destroyed")
            }

        }

        val debugObj = Object()
        val debugBehaviour = DebugBehaviour()
        debugObj.addComponent(debugBehaviour)
        debugObj.addComponent(InputController())

        scene.attachToRoot(debugObj)

        val camera = PerspectiveCamera(1280f, 720f, 70f, 0.1f, 10000f)
        //debugObj.getComponent<Transform>()!!.setScale(Vector3(30f))
        //debugObj.getComponent<Transform>()!!.setRotation(Vector3(90.0f.toRadians(), 180f.toRadians(), 0.0f))
        debugObj.addComponent(camera)
        debugObj.getComponent<Transform>()!!.setTranslation(Vector3(0f, 300f, 0f))
        //debugObj.getComponent<Transform>()!!.setRotation(Vector3(90f.toRadians(), 0f, 0f))
        debugObj.getComponent<BoxAABB>()!!.setShape(
            Rect3d(Vector3(1f), Vector3(3f))
        )

        val cameraController = CameraController(10.0f, 0.1f)
        debugObj.addComponent(cameraController)

        Object.services.putService<Camera>(camera)

        val worldScale = Vector3(1600.0f, 360.0f, 1600.0f)
        val worldOffset = Vector3(0f)

        val heightTexture = Texture2d(
            Object.services.getService<ImageLoader>()!!.loadImage("textures/heightmap/hm0.bmp")
        )

        val heightmap = Heightmap(heightTexture, worldScale, worldOffset)
        val tiledTerrain = TiledTerrain(
            TiledTerrainConfig(
                heightmap,
                16,
                worldScale,
                worldOffset
            )
        )
        scene.attachToRoot(tiledTerrain)

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