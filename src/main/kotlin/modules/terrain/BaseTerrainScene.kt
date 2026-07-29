package modules.terrain

import core.management.Resources
import core.math.Vector3
import core.math.extensions.toRadians
import core.scene.Object
import core.scene.SceneGraph
import core.scene.SceneInterface
import core.scene.Transform
import core.scene.camera.Camera
import core.scene.camera.CameraController
import core.scene.camera.OrthographicCamera
import core.scene.camera.PerspectiveCamera
import graphics.rendering.context.RenderContextController
import graphics.rendering.postproc.PostProcessor
import graphics.rendering.postproc.godrays.GodraysPPEffect
import graphics.tools.MonitoringBehaviour
import graphics.tools.PickingBehaviour
import modules.light.AtmosphereController
import modules.light.SunLightController
import modules.light.defaultSunScreenPositionProvider
import modules.light.flare.LensFlare
import modules.sky.dome.SkyDome
import modules.sky.dome.SkyDomeParams
import modules.terrain.heightmap.Heightmap
import modules.terrain.navigation.TerrainAgentController
import modules.terrain.objects.fauna.Butterfly
import modules.terrain.objects.flora.grass.Grass
import modules.terrain.objects.flora.trees.TreeSet
import modules.terrain.objects.rocks.RockSet
import modules.water.ocean.Ocean
import modules.water.ocean.OceanParams
import modules.water.plane.WaterPlane
import modules.water.plane.WaterPlaneParams
import kotlin.math.max

abstract class BaseTerrainScene(
    protected val params: TerrainSceneParams
) : SceneInterface {
    protected lateinit var scene: SceneGraph

    override fun oneTimeSceneInit(): SceneGraph {
        scene = SceneGraph(
            params.worldBounds
        )

        val mainObj = Object()
        mainObj.addComponent(SunLightController())
        mainObj.addComponent(AtmosphereController())
        mainObj.addComponent(RenderContextController())
        mainObj.addComponent(PickingBehaviour())
        mainObj.addComponent(MonitoringBehaviour())
        scene.attachToRoot(mainObj)

        val camera = PerspectiveCamera(
            params.cameraSettings.width.toFloat(),
            params.cameraSettings.height.toFloat(),
            params.cameraSettings.fov,
            params.cameraSettings.zNear,
            params.cameraSettings.zFar
        )
        Resources.put<Camera>(camera)

        val cameraObj = Object()
        cameraObj.addComponent(camera)
        cameraObj.getComponent<Transform>()!!.setTranslation(params.cameraSettings.initialTranslation)
        cameraObj.getComponent<Transform>()!!.setRotation(params.cameraSettings.initialRotation)

        val cameraController = CameraController(
            params.cameraSettings.moveSpeed,
            params.cameraSettings.rotationSpeed,
            params.cameraSettings.sensitivity
        )
        cameraObj.addComponent(cameraController)
        scene.attachToRoot(cameraObj)

        val orthoScale = max(params.worldScale.x, params.worldScale.z)
        val orthographicCamera = OrthographicCamera(
            -orthoScale,
            orthoScale,
            -orthoScale,
            orthoScale,
            -orthoScale,
            orthoScale
        )
        Resources.put<OrthographicCamera>(orthographicCamera)

        initializeTerrain()

        return scene
    }

    abstract fun initializeTerrain()

    fun withSkyDome(params: SkyDomeParams = SkyDomeParams(), postProcessing: Boolean = false): BaseTerrainScene {
        scene.attachToRoot(SkyDome(SkyDomeParams(), postProcessing))

        return this
    }

    fun withLensFlare(): BaseTerrainScene {
        val lensFlare = LensFlare()
        scene.attachToRoot(lensFlare)

        return this
    }

    fun withWaterPlane(scaleY: Float = 1.0f): BaseTerrainScene {
        val waterPlane = WaterPlane(WaterPlaneParams())
        waterPlane.getComponent<Transform>()!!
            .setScale(
                Vector3(
                    params.worldScale.x,
                    scaleY,
                    params.worldScale.z
                )
            )
        waterPlane.getComponent<Transform>()!!
            .setTranslation(params.worldOffset)
        scene.attachToRoot(waterPlane)

        return this
    }

    fun withOcean(oceanParams: OceanParams, stretchToHorizon: Boolean = true): BaseTerrainScene {
        val ocean = Ocean(oceanParams, stretchToHorizon)
        ocean.getComponent<Transform>()!!.setScale(params.worldScale)
        scene.attachToRoot(ocean)

        return this
    }

    fun withGrass(): BaseTerrainScene {
        scene.attachToRoot(Grass())

        return this
    }

    fun withFlora(): BaseTerrainScene {
        val trees = TreeSet(false)
        trees.getComponent<Transform>()!!.setScale(Vector3(1f, 1f, 1f))
        scene.attachToRoot(trees)

        val rocks = RockSet()
        trees.getComponent<Transform>()!!.setScale(Vector3(1f, 1f, 1f))
        scene.attachToRoot(rocks)

        return this
    }

    fun withFauna(gridSize: Float = 5.0f, maxSlope: Float = 0.35f): BaseTerrainScene {
        val terrainNavMesh = Object()

        val heightmap = Resources.get<Heightmap>()!!
        val camera = Resources.get<Camera>()!!

        val terrainAgentController = TerrainAgentController(
            heightmap,
            camera,
            gridSize,
            maxSlope
        )
        terrainNavMesh.addComponent(terrainAgentController)
        scene.attachToRoot(terrainNavMesh)

        val butterfly = Butterfly()
        butterfly.getComponent<Transform>()!!.setTranslation(Vector3(50f, 100f, 50f))
        //butterfly.getComponent<Transform>()!!.setScale(Vector3(1.01f))
        butterfly.getComponent<Transform>()!!.setRotation(Vector3(0f, 0f, 90f.toRadians()))
        scene.attachToRoot(butterfly)

        return this
    }

    fun withPostProcessing(): BaseTerrainScene {
        PostProcessor.add(GodraysPPEffect(::defaultSunScreenPositionProvider))

        return this
    }
}