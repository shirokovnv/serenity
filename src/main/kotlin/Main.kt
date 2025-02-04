import core.management.Resources
import core.math.Rect3d
import core.math.Vector3
import core.math.extensions.toRadians
import core.scene.Object
import core.scene.SceneGraph
import core.scene.Transform
import core.scene.camera.Camera
import core.scene.camera.CameraController
import core.scene.camera.OrthographicCamera
import core.scene.camera.PerspectiveCamera
import graphics.rendering.context.RenderContextController
import graphics.rendering.postproc.PostProcessor
import graphics.rendering.postproc.godrays.GodraysPPEffect
import graphics.tools.MonitoringBehaviour
import modules.fauna.Butterfly
import modules.flora.grass.Grass
import modules.flora.trees.TreeSet
import modules.light.AtmosphereController
import modules.light.SunLightController
import modules.light.defaultSunScreenPositionProvider
import modules.light.flare.LensFlare
import modules.ocean.Ocean
import modules.ocean.OceanParams
import modules.sky.SkyDome
import modules.sky.SkyDomeParams
import modules.terrain.heightmap.DiamondSquareGenerator
import modules.terrain.heightmap.DiamondSquareParams
import modules.terrain.heightmap.Heightmap
import modules.terrain.tiled.TiledTerrain
import modules.terrain.tiled.TiledTerrainConfig
import platform.Application
import platform.ApplicationSettings
import kotlin.math.max

val settings = ApplicationSettings(
    1280,
    720,
    200.0f,
    "Serenity Engine - OPENGL"
)

class App(settings: ApplicationSettings) : Application(settings) {
    override fun oneTimeSceneInit(): SceneGraph {
        val scene = SceneGraph(
            Rect3d(
                Vector3(-3000f),
                Vector3(3000f)
            )
        )
        val worldScale = Vector3(1600.0f, 360.0f, 1600.0f)
        val worldOffset = Vector3(0f)

        val mainObj = Object()
        mainObj.addComponent(SunLightController())
        mainObj.addComponent(AtmosphereController())
        mainObj.addComponent(RenderContextController())
        mainObj.addComponent(MonitoringBehaviour())
        scene.attachToRoot(mainObj)

        val camera = PerspectiveCamera(
            settings.screenWidth.toFloat(),
            settings.screenHeight.toFloat(),
            70f,
            0.1f,
            3000f
        )
        Resources.put<Camera>(camera)

        val cameraObj = Object()
        cameraObj.addComponent(camera)
        cameraObj.getComponent<Transform>()!!.setTranslation(Vector3(0f, 300f, 0f))
        cameraObj.getComponent<Transform>()!!.setRotation(Vector3(0f, 90f.toRadians(), 0f))

        val cameraController = CameraController(0.5f, 1.5f, 0.1f)
        cameraObj.addComponent(cameraController)
        scene.attachToRoot(cameraObj)

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

        val heightmap = Heightmap.fromGenerator(
            DiamondSquareGenerator(),
            DiamondSquareParams(2f, 40f),
            1024,
            1024,
            worldOffset,
            worldScale
        )
        Resources.put<Heightmap>(heightmap)

        val tiledTerrain = TiledTerrain(
            TiledTerrainConfig(
                heightmap,
                16,
                worldScale,
                worldOffset
            ), false
        )
        scene.attachToRoot(tiledTerrain)

        val trees = TreeSet(false)
        trees.getComponent<Transform>()!!.setScale(Vector3(1f, 1f, 1f))
        scene.attachToRoot(trees)

        val grass = Grass()
        scene.attachToRoot(grass)

        val butterfly = Butterfly()
        butterfly.getComponent<Transform>()!!.setTranslation(Vector3(50f, 100f, 50f))
        butterfly.getComponent<Transform>()!!.setRotation(Vector3(0f, 0f, 90f.toRadians()))
        scene.attachToRoot(butterfly)

        val oceanParams = OceanParams(
            512,
            256,
            10.0f,
            45.0f,
            10.0f,
            0.5f
        )

        val ocean = Ocean(oceanParams, true)
        ocean.getComponent<Transform>()!!.setScale(worldScale)
        scene.attachToRoot(ocean)
        scene.attachToRoot(SkyDome(SkyDomeParams(), false))

        val lensFlare = LensFlare()
        scene.attachToRoot(lensFlare)

        // Post Processing
        //PostProcessor.add(GodraysPPEffect(::defaultSunScreenPositionProvider))

        return scene
    }
}

fun main() {
    val app = App(settings)

    app.launch()
}