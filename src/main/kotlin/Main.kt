import core.ecs.Behaviour
import core.management.Resources
import core.math.Rect3d
import core.math.Vector3
import core.math.extensions.toRadians
import core.scene.BoxAABB
import core.scene.Object
import core.scene.SceneGraph
import core.scene.Transform
import core.scene.camera.*
import core.scene.spatial.LinearQuadTree
import core.scene.spatial.SpatialHashGrid
import graphics.assets.texture.Texture2d
import graphics.assets.texture.TextureFactory
import modules.flora.palm.Palm
import modules.light.AtmosphereController
import modules.light.SunLightController
import modules.light.flare.LensFlare
import modules.ocean.Ocean
import modules.sky.SkyDome
import modules.terrain.heightmap.Heightmap
import modules.terrain.heightmap.DiamondSquareGenerator
import modules.terrain.heightmap.DiamondSquareParams
import modules.terrain.tiled.TiledTerrain
import modules.terrain.tiled.TiledTerrainConfig
import platform.Application
import platform.ApplicationSettings
import platform.services.filesystem.ImageLoader
import kotlin.math.max

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
        debugObj.addComponent(SunLightController())
        debugObj.addComponent(AtmosphereController())

        scene.attachToRoot(debugObj)

        val camera = PerspectiveCamera(1280f, 720f, 70f, 0.1f, 3000f)
        debugObj.addComponent(camera)
        debugObj.getComponent<Transform>()!!.setTranslation(Vector3(0f, 300f, 0f))
        debugObj.getComponent<Transform>()!!.setRotation(Vector3(0f, 90f.toRadians(), 0f))
        debugObj.getComponent<BoxAABB>()!!.setShape(
            Rect3d(Vector3(1f), Vector3(3f))
        )

        val cameraController = CameraController(0.5f, 1.5f, 0.1f)
        debugObj.addComponent(cameraController)

        Resources.put<Camera>(camera)

        val worldScale = Vector3(1600.0f, 360.0f, 1600.0f)
        val worldOffset = Vector3(0f)

        val orthoScale = max(worldScale.x, worldScale.z)
        val orthographicCamera = OrthographicCamera(
            -orthoScale,
            orthoScale,
            -orthoScale,
            orthoScale,
            -orthoScale,
            orthoScale
        )
        Resources.put<OrthographicCamera>(orthographicCamera)

        val heightTexture = Texture2d(
            Resources.get<ImageLoader>()!!.loadImage("textures/heightmap/hm0.bmp")
        )

        val heightmap = Heightmap(heightTexture, worldScale, worldOffset)
        val randomHeightmap = Heightmap(TextureFactory.fromPerlinNoise(
            1024,
            1024,
            0.01f,
            5,
            0.3f,
            0.3f
        ), worldScale, worldOffset)
        val diamondSquareHeightmap = Heightmap.fromGenerator(
            DiamondSquareGenerator(),
            DiamondSquareParams(2f, 40f),
            1024,
            1024,
            worldOffset,
            worldScale
        )
        val tiledTerrain = TiledTerrain(
            TiledTerrainConfig(
                randomHeightmap,
                16,
                worldScale,
                worldOffset
            )
        )
        scene.attachToRoot(tiledTerrain)

        Resources.put<Heightmap>(randomHeightmap)

        val palm = Palm()
        palm.getComponent<Transform>()!!.setScale(Vector3(1f, 1f, 1f))
        //palm.getComponent<Transform>()!!.setTranslation(Vector3(0f, 100f, 0f))
        scene.attachToRoot(palm)

        val ocean = Ocean()
        ocean.getComponent<Transform>()!!.setScale(worldScale)
        scene.attachToRoot(ocean)
        scene.attachToRoot(SkyDome())

        val lensFlare = LensFlare()
        scene.attachToRoot(lensFlare)


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