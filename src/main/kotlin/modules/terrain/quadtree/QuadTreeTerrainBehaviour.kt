package modules.terrain.quadtree

import core.ecs.Behaviour
import core.events.Events
import core.management.Resources
import core.math.Matrix4
import core.math.Vector2
import core.scene.Transform
import core.scene.camera.Camera
import core.scene.camera.Frustum
import core.scene.camera.PerspectiveCamera
import graphics.assets.buffer.DepthBufferType
import graphics.assets.buffer.Fbo
import graphics.assets.surface.bind
import graphics.rendering.Colors
import graphics.rendering.gizmos.DrawGizmosEvent
import modules.light.SunLightManager
import modules.terrain.TerrainBlendRenderer
import modules.terrain.TerrainNormalRenderer
import modules.terrain.quadtree.gizmos.QuadTreeBoundsDrawer
import modules.terrain.quadtree.gizmos.QuadTreeBoundsMaterial
import modules.terrain.quadtree.gizmos.QuadTreeBoundsShader
import modules.terrain.quadtree.top_view.QuadTreeTopViewMaterial
import modules.terrain.quadtree.top_view.QuadTreeTopViewRenderer
import modules.terrain.quadtree.top_view.QuadTreeTopViewShader

class QuadTreeTerrainBehaviour(
    private val terrConfig: QuadTreeTerrainConfig,
    private val lodConfig: QuadTreeLoDConfig
) : Behaviour() {

    private lateinit var rootNode: QuadTreeTerrainNode
    private lateinit var buffer: QuadTreeBuffer
    private lateinit var shader: QuadTreeTerrainShader
    private lateinit var material: QuadTreeTerrainMaterial
    private lateinit var gui: QuadTreeTerrainGui

    private lateinit var topViewFbo: Fbo
    private lateinit var topViewShader: QuadTreeTopViewShader
    private lateinit var topViewMaterial: QuadTreeTopViewMaterial

    private lateinit var boundsMaterial: QuadTreeBoundsMaterial
    private lateinit var boundsShader: QuadTreeBoundsShader

    private lateinit var camera: Camera
    private lateinit var frustum: Frustum

    private var lastCleanupTimeMs = System.currentTimeMillis()
    private val cleanupIntervalMs = 10_000L

    private val transform: Transform
        get() = owner()!!.getComponent<Transform>()!!

    private val sunLightManager: SunLightManager
        get() = Resources.get<SunLightManager>()!!

    override fun create() {
        rootNode = QuadTreeTerrainNode(
            terrConfig,
            Vector2(0.0f, 0.0f),
            0,
            lodConfig
        )

        transform.setScale(terrConfig.worldScale)
        transform.setTranslation(terrConfig.worldOffset)

        camera = Resources.get<Camera>()!!
        frustum = Frustum(camera as PerspectiveCamera)

        buffer = QuadTreeBuffer(
            patchVertices()
        )
        shader = QuadTreeTerrainShader()
        material = QuadTreeTerrainMaterial()
        shader bind material
        shader.setup()

        material.normalmap = owner()!!.getComponent<TerrainNormalRenderer>()!!.getMaterial().normalmap
        material.blendmap = owner()!!.getComponent<TerrainBlendRenderer>()!!.getMaterial().blendmap

        val renderer = QuadTreeTerrainRenderer(rootNode, buffer, shader, material)
        owner()!!.addComponent(renderer)

        topViewMaterial = QuadTreeTopViewMaterial()
        topViewShader = QuadTreeTopViewShader()
        topViewShader bind topViewMaterial
        topViewShader.setup()

        topViewFbo = Fbo(
            256,
            256,
            DepthBufferType.NONE
        )

        val topViewRenderer = QuadTreeTopViewRenderer(buffer, topViewFbo, topViewMaterial, topViewShader)
        owner()!!.addComponent(topViewRenderer)

        boundsMaterial = QuadTreeBoundsMaterial()
        boundsShader = QuadTreeBoundsShader()
        boundsShader bind boundsMaterial
        boundsShader.setup()

        val boundsDrawer = QuadTreeBoundsDrawer(
            buffer,
            boundsMaterial,
            boundsShader
        )
        owner()!!.addComponent(boundsDrawer)

        gui = QuadTreeTerrainGui(rootNode, terrConfig, lodConfig, topViewFbo.getColorTexture().getId())
        owner()!!.addComponent(gui)

        Events.subscribe<DrawGizmosEvent, Any>(::onDrawGizmos)

        println("TERRAIN QUADTREE INITIALIZED")
    }

    override fun update(deltaTime: Float) {
        val now = System.currentTimeMillis()
        if (now - lastCleanupTimeMs >= cleanupIntervalMs) {
            println("HITS: " + QuadTreeTerrainNode.quadTreeCache.hits())
            println("ALLOC: " + QuadTreeTerrainNode.quadTreeCache.allocations())
            println("CLEANUP")
            QuadTreeTerrainNode.quadTreeCache.cleanupExpired()
            lastCleanupTimeMs = now
        }

        frustum.recalculatePlanes()
        frustum.recalculateSearchVolume()
        rootNode.recursiveUpdate(camera, frustum)

        material.apply {
            heightmap = terrConfig.heightmap
            model = Matrix4().identity()
            world = transform.matrix()
            viewProjection = camera.viewProjection
            scaleY = terrConfig.worldScale.y
            tessFactor = lodConfig.tessFactor
        }
        material.sunColor = sunLightManager.sunColor()
        material.sunVector = sunLightManager.sunVector()
        material.sunIntensity = sunLightManager.sunIntensity()
        material.camPos = camera.position()

        boundsMaterial.apply {
            viewProj = camera.viewProjection
            color = Colors.Blue
        }

        topViewMaterial.apply {
            viewProj = camera.viewProjection
        }
    }

    override fun destroy() {
        Events.unsubscribe<DrawGizmosEvent, Any>(::onDrawGizmos)

        rootNode.clear()
        buffer.destroy()
        shader.destroy()
        topViewShader.destroy()
        topViewFbo.destroy()
        boundsShader.destroy()
    }

    private fun patchVertices(): Array<Vector2> {
        return arrayOf(
            Vector2(0f, 0f),
            Vector2(0f, 1f),
            Vector2(1f, 0f),
            Vector2(1f, 1f)
        )
    }

    private fun onDrawGizmos(event: DrawGizmosEvent, sender: Any) {
        owner()!!.getComponent<QuadTreeBoundsDrawer>()?.draw()
    }
}