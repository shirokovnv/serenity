package modules.terrain.marching_cubes

import core.ecs.Behaviour
import core.events.Events
import core.management.Resources
import core.math.Matrix4
import core.math.Vector3
import core.math.extensions.saturate
import core.math.noise.SimplexNoise
import core.scene.Object
import core.scene.Transform
import core.scene.camera.Camera
import core.scene.voxelization.ScalarField
import graphics.assets.surface.bind
import graphics.rendering.Colors
import graphics.rendering.Renderer
import graphics.rendering.gizmos.BoxAABBDrawer
import graphics.rendering.gizmos.DrawGizmosEvent
import graphics.rendering.gizmos.NormalDrawer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import modules.light.SunLightManager

class MarchingCubesBehaviour : Behaviour(), Renderer {

    private lateinit var buffer: MarchingCubesBuffer
    private lateinit var material: MarchingCubesMaterial
    private lateinit var shader: MarchingCubesShader

    private val camera: Camera
        get() = Resources.get<Camera>()!!

    private val world: Matrix4
        get() = (owner() as Object).worldMatrix()

    private val sunLightManager: SunLightManager
        get() = Resources.get<SunLightManager>()!!

    private var gridParams = MarchingCubesGridParams(
        100,
        0.1f
    )

    private var noiseParams = MarchingCubesNoiseParams(
        1.0f,
        1.0f,
        2.0f,
        0.7f,
        1
    )

    private var extraParams = MarchingCubesExtraParams(
        false,
        1.0f,
        false,
        8,
        false,
        1.5f,
        5f,
        Vector3(0.45f, 0.95f, 0.95f),
        Vector3(0.4f, 0.4f, 0.42f),
        0.5f
    )

    private var noise = SimplexNoise(
        noiseParams.frequency,
        noiseParams.amplitude,
        noiseParams.lacunarity,
        noiseParams.persistence
    )

    private val scale = Vector3(5f, 2f, 5f)

    fun getResolution(): Int = gridParams.resolution

    override fun create() {
        val mesh = rebuildMesh()

        buffer = MarchingCubesBuffer()
        buffer.uploadData(mesh.vertices, mesh.normals, mesh.occlusions)
        material = MarchingCubesMaterial()
        shader = MarchingCubesShader()
        shader bind material
        shader.setup()

        mesh.cleanUp()

        rescaleMesh()

        owner()?.addComponent(MarchingCubesGui(gridParams, noiseParams, extraParams))
        owner()?.addComponent(BoxAABBDrawer(Colors.LightGray))
        owner()?.addComponent(NormalDrawer(buffer, { world }, { camera.viewProjection }))
        (owner() as Object).recalculateBounds()

        Events.subscribe<MarchingCubesChangedEvent, Any>(::onChanged)
        Events.subscribe<DrawGizmosEvent, Any>(::onDrawGizmos)

        println("MARCHING CUBES BEHAVIOUR INITIALIZED")
    }

    override fun update(deltaTime: Float) {
    }

    override fun destroy() {
        Events.unsubscribe<MarchingCubesChangedEvent, Any>(::onChanged)
        Events.unsubscribe<DrawGizmosEvent, Any>(::onDrawGizmos)

        owner()?.getComponent<BoxAABBDrawer>()?.dispose()
        owner()?.getComponent<NormalDrawer>()?.dispose()

        shader.destroy()
        buffer.destroy()
    }

    override fun render(pass: RenderPass) {
        material.world = world
        material.viewProjection = camera.viewProjection
        material.lightDirection = sunLightManager.sunVector()
        material.lightColor = sunLightManager.sunColor()
        material.lightIntensity = sunLightManager.sunIntensity()
        material.colorOne = extraParams.colorOne
        material.colorTwo = extraParams.colorTwo
        material.ambientOcclusion = extraParams.ambientOcclusion
        material.resolution = gridParams.resolution

        shader.bind()
        shader.updateUniforms()
        buffer.draw()
        shader.unbind()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }

    private fun onDrawGizmos(event: DrawGizmosEvent, sender: Any) {
        owner()?.getComponent<BoxAABBDrawer>()?.draw()
        owner()?.getComponent<NormalDrawer>()?.draw()
    }

    private fun onChanged(event: MarchingCubesChangedEvent, sender: Any) {
        gridParams = event.gridParams
        noiseParams = event.noiseParams
        extraParams = event.extraParams

        noise = SimplexNoise(
            noiseParams.frequency,
            noiseParams.amplitude,
            noiseParams.lacunarity,
            noiseParams.persistence
        )

        if (event.meshParamsChanged) {
            val mesh = rebuildMesh()
            buffer.uploadData(mesh.vertices, mesh.normals, mesh.occlusions)
            mesh.cleanUp()

            rescaleMesh()
        }

        (owner() as Object).recalculateBounds()
    }

    private fun rebuildMesh(): MarchingCubesMeshData {
        val scalarField = ScalarField(gridParams.resolution, ::densityProvider)
        val voxelGrid = scalarField.generate()
        val generator = MarchingCubesGenerator(voxelGrid, gridParams.isoLevel, ::densityProvider)

        return generator.generateMesh()
    }

    private fun rescaleMesh() {
        val resFactor = MarchingCubesGridParams.MAX_RESOLUTION / gridParams.resolution.toFloat()

        owner()?.getComponent<Transform>()?.setScale(scale * resFactor)
    }

    private fun densityProvider(x: Float, y: Float, z: Float): Float {
        val ws = Vector3(x, y, z)

        var density = -ws.y

        density += noise.fractal(noiseParams.octaves, x, y, z)

        if (extraParams.isTerracingEnabled) {
            //density += ws.y % extraParams.terraceHeight
            density += ((extraParams.terraceHeight - ws.y) * 1f).saturate() * 2.0f
        }

        if (extraParams.isWarpingEnabled) {
            val wp = Vector3(ws) * 0.004f
            val warp = noise.fractal(noiseParams.octaves, wp.x, wp.y, wp.z)
            density += warp * extraParams.warpFactor
        }

        if (extraParams.isPlanetizingEnabled) {
            val center = Vector3(0.5f)
            density += extraParams.planetRadius - (center - ws).length() * extraParams.planetStrength
        }

        return density
    }
}