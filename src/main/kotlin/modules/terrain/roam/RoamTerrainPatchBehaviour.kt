package modules.terrain.roam

import core.events.Events
import core.management.Resources
import core.math.Matrix4
import core.scene.Transform
import core.scene.behaviour.FrameUpdateBehaviour
import core.scene.camera.Camera
import graphics.assets.surface.bind
import graphics.rendering.Colors
import graphics.rendering.gizmos.DrawGizmosEvent
import modules.light.SunLightManager
import modules.terrain.roam.gizmos.RoamPatchBoundsDrawer
import modules.terrain.roam.gizmos.RoamPatchBoundsMaterial
import modules.terrain.roam.gizmos.RoamPatchBoundsShader
import modules.terrain.roam.gizmos.RoamPatchRootDrawer
import modules.terrain.roam.gui.RoamTerrainPatchGui

class RoamTerrainPatchBehaviour(
    private val config: RoamTerrainPatchConfig
) : FrameUpdateBehaviour() {
    private lateinit var shader: RoamTerrainPatchShader
    private lateinit var material: RoamTerrainPatchMaterial
    private lateinit var renderer: RoamTerrainPatchRenderer
    private lateinit var gui: RoamTerrainPatchGui

    private lateinit var boundsShader: RoamPatchBoundsShader
    private lateinit var boundsMaterial: RoamPatchBoundsMaterial

    private val patch: RoamTerrainPatch
        get() = owner()!!.getComponent<RoamTerrainPatch>()!!

    private val transform: Transform
        get() = owner()!!.getComponent<Transform>()!!

    private val camera: Camera
        get() = Resources.get<Camera>()!!

    private val sunLightManager: SunLightManager
        get() = Resources.get<SunLightManager>()!!

    override fun create() {
        shader = RoamTerrainPatchShader()
        material = RoamTerrainPatchMaterial()
        shader bind material
        shader.setup()

        material.model = Matrix4().identity()

        renderer = RoamTerrainPatchRenderer(
            patch.buffer(),
            shader,
            material,
            patch.metrics()
        )

        owner()!!.addComponent(renderer)
        gui = RoamTerrainPatchGui(config, patch.metrics(), camera)
        owner()!!.addComponent(gui)

        boundsMaterial = RoamPatchBoundsMaterial()
        boundsMaterial.model = Matrix4().identity()
        boundsMaterial.color = Colors.Blue
        boundsShader = RoamPatchBoundsShader()
        boundsShader bind boundsMaterial
        boundsShader.setup()
        owner()!!.addComponent(RoamPatchBoundsDrawer(patch.buffer(), boundsShader, boundsMaterial))
        setupMaterials()

        owner()!!.addComponent(RoamPatchRootDrawer(patch))

        Events.subscribe<DrawGizmosEvent, Any>(::onDrawGizmos)

        println("TERRAIN ROAM PATCH INITIALIZED")
    }

    override fun onUpdate(deltaTime: Float) {
        setupMaterials()
        patch.update()
    }

    override fun destroy() {
        Events.unsubscribe<DrawGizmosEvent, Any>(::onDrawGizmos)

        owner()?.getComponent<RoamPatchRootDrawer>()?.dispose()
        patch.dispose()
        shader.destroy()
        boundsShader.destroy()
    }

    private fun onDrawGizmos(event: DrawGizmosEvent, sender: Any) {
        owner()?.getComponent<RoamPatchRootDrawer>()?.draw()
        owner()?.getComponent<RoamPatchBoundsDrawer>()?.draw()
    }

    private fun setupMaterials() {
        material.apply {
            world = transform.matrix()
            viewProjection = camera.viewProjection
            cameraPosition = camera.position()
            heightmap = patch.heightmap()
            sunVector = sunLightManager.sunVector()
            sunIntensity = sunLightManager.sunIntensity()
        }

        boundsMaterial.apply {
            world = transform.matrix()
            viewProj = camera.viewProjection
            heightmap = patch.heightmap()
        }
    }
}