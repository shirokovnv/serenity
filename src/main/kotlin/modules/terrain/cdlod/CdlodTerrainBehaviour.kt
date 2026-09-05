package modules.terrain.cdlod

import core.ecs.Behaviour
import core.management.Resources
import core.math.Matrix4
import core.scene.Transform
import core.scene.camera.Camera
import core.scene.camera.Frustum
import core.scene.camera.PerspectiveCamera
import graphics.assets.surface.bind
import modules.light.SunLightManager
import modules.terrain.TerrainBlendRenderer
import modules.terrain.TerrainNormalRenderer

class CdlodTerrainBehaviour(private val config: CdlodTerrainConfig) : Behaviour() {
    private lateinit var terrain: CdlodTerrainSystem
    private lateinit var buffer: CdlodTerrainBuffer
    private lateinit var shader: CdlodTerrainShader
    private lateinit var material: CdlodTerrainMaterial
    private lateinit var renderer: CdlodTerrainRenderer

    private val camera: Camera
        get() = Resources.get<Camera>()!!

    private lateinit var frustum: Frustum

    private val transform: Transform
        get() = owner()!!.getComponent<Transform>()!!

    private val sunLightManager: SunLightManager
        get() = Resources.get<SunLightManager>()!!

    private val onResolutionChange = { newRes: Int ->
        buffer.destroy()
        buffer.setGridSize(newRes + 1)
        buffer.create()
    }

    private val onDistanceMultiplierChange = { newMult: Float ->
        terrain.calculateLodRanges()
    }

    override fun create() {
        terrain = CdlodTerrainSystem(config)

        transform.setScale(config.worldScale)
        transform.setTranslation(config.worldOffset)

        buffer = CdlodTerrainBuffer(config.resolution + 1)
        shader = CdlodTerrainShader()
        material = CdlodTerrainMaterial()
        material.resolution = config.resolution.toFloat()
        material.lodRanges = terrain.lodRanges()

        shader bind material
        shader.setup()

        renderer = CdlodTerrainRenderer(terrain, buffer, shader, material)
        owner()!!.addComponent(renderer)

        owner()!!.addComponent(CdlodTerrainGui(config))

        frustum = Frustum(camera as PerspectiveCamera)

        config.addOnDistanceMultiplierChanged(onDistanceMultiplierChange)
        config.addOnResolutionChanged(onResolutionChange)
    }

    override fun update(deltaTime: Float) {
        frustum.recalculatePlanes()
        frustum.recalculateSearchVolume()

        terrain.update(camera, frustum)

        material.apply {
            heightmap = config.heightmap
            model = Matrix4().identity()
            world = transform.matrix()
            viewProjection = camera.viewProjection
            resolution = config.resolution.toFloat()
            lodRanges = terrain.lodRanges()
            camPos = camera.position()
            sunVector = sunLightManager.sunVector()
            sunColor = sunLightManager.sunColor()
            sunIntensity = sunLightManager.sunIntensity()
        }

        material.normalmap = owner()!!.getComponent<TerrainNormalRenderer>()!!.getMaterial().normalmap
        material.blendmap = owner()!!.getComponent<TerrainBlendRenderer>()!!.getMaterial().blendmap
    }

    override fun destroy() {
        shader.destroy()
        buffer.destroy()

        config.removeOnDistanceMultiplierChanged(onDistanceMultiplierChange)
        config.removeOnResolutionChanged(onResolutionChange)
    }
}