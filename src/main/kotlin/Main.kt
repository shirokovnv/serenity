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
import graphics.particles.ParticleBehaviour
import graphics.rendering.context.RenderContextController
import graphics.tools.MonitoringBehaviour
import graphics.tools.PickingBehaviour
import modules.terrain.objects.fauna.Butterfly
import modules.terrain.objects.flora.grass.Grass
import modules.terrain.objects.flora.trees.TreeSet
import modules.light.AtmosphereController
import modules.light.SunLightController
import modules.light.flare.LensFlare
import modules.sky.box.SkyBox
import modules.water.ocean.OceanParams
import modules.sky.dome.SkyDome
import modules.sky.dome.SkyDomeParams
import modules.terrain.heightmap.*
import modules.terrain.marching_cubes.MarchingCubes
import modules.terrain.navigation.TerrainAgentController
import modules.terrain.objects.rocks.RockSet
import modules.terrain.tiled.TiledTerrain
import modules.terrain.tiled.TiledTerrainConfig
import modules.water.plane.WaterPlane
import modules.water.plane.WaterPlaneParams
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
        mainObj.addComponent(PickingBehaviour())
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

        val rocks = RockSet()
        trees.getComponent<Transform>()!!.setScale(Vector3(1f, 1f, 1f))
        scene.attachToRoot(rocks)

//        val marchingCubes = MarchingCubes()
//        marchingCubes.getComponent<Transform>()!!.setTranslation(Vector3(-500f, 0f, -500f))
//        scene.attachToRoot(marchingCubes)

        // Grass
        // scene.attachToRoot(Grass())

        val butterfly = Butterfly()
        butterfly.getComponent<Transform>()!!.setTranslation(Vector3(50f, 100f, 50f))
        //butterfly.getComponent<Transform>()!!.setScale(Vector3(1.01f))
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

//        val ocean = Ocean(oceanParams, true)
//        ocean.getComponent<Transform>()!!.setScale(worldScale)
//        scene.attachToRoot(ocean)

        // Water plane
        val waterPlane = WaterPlane(WaterPlaneParams())
        waterPlane.getComponent<Transform>()!!
            .setScale(
                Vector3(
                worldScale.x,
                1.0f,
                worldScale.z
            )
            )
        waterPlane.getComponent<Transform>()!!
            .setTranslation(worldOffset)
        scene.attachToRoot(waterPlane)

        // SkyDome
        scene.attachToRoot(SkyDome(SkyDomeParams(), false))
//        scene.attachToRoot(SkyBox())

        val lensFlare = LensFlare()
        scene.attachToRoot(lensFlare)

        // particles
        val particle = Object()
        particle.getComponent<Transform>()!!.setTranslation(Vector3(150f))
        particle.addComponent(ParticleBehaviour())
        scene.attachToRoot(particle)

        // TODO: move to specific object
        val terrainNavMesh = Object()
        val terrainAgentController = TerrainAgentController(
            heightmap,
            camera,
            5.0f,
            0.35f
        )
        terrainNavMesh.addComponent(terrainAgentController)
        scene.attachToRoot(terrainNavMesh)

        // Post Processing
        //PostProcessor.add(GodraysPPEffect(::defaultSunScreenPositionProvider))

        return scene
    }
}

fun main() {
    val app = App(settings)

    app.launch()
}