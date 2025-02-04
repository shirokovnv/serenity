package modules.terrain.tiled

import core.ecs.Behaviour
import core.events.Events
import core.management.Resources
import core.math.Matrix4
import core.math.Vector2
import core.scene.Object
import core.scene.Transform
import core.scene.camera.Camera
import core.scene.camera.OrthographicCamera
import core.scene.volumes.BoxAABB
import graphics.assets.surface.bind
import graphics.assets.texture.Texture2d
import graphics.rendering.Colors
import graphics.rendering.gizmos.BoxAABBDrawer
import graphics.rendering.gizmos.DrawGizmosEvent
import graphics.rendering.shadows.ShadowFrameBuffer
import modules.light.SunLightManager
import modules.terrain.TerrainBlendRenderer
import modules.terrain.TerrainNormalRenderer
import platform.services.filesystem.ImageLoader

class TiledTerrainBehaviour(
    private val config: TiledTerrainConfig,
    private val enablePostProcessing: Boolean = true
) : Behaviour() {
    private lateinit var material: TiledTerrainMaterial
    private lateinit var shader: TiledTerrainShader
    private lateinit var buffer: TiledTerrainBuffer
    private lateinit var renderer: TiledTerrainRenderer
    private lateinit var ppRenderer: TiledTerrainPPRenderer

    private val transform: Transform
        get() = owner()!!.getComponent<Transform>()!!

    private val lightViewProjection: Matrix4
        get() {
            val sunLightManager = Resources.get<SunLightManager>()!!
            val orthographicCamera = Resources.get<OrthographicCamera>()!!

            val view = sunLightManager.calculateLightViewMatrix()
            val projection = orthographicCamera.projection

            return projection * view
        }

    override fun create() {
        material = TiledTerrainMaterial()
        shader = TiledTerrainShader()
        shader bind material

        val vertices = buildVertices()

        // INSTANCING
        val numRows = config.gridSize
        val numCols = config.gridSize
        val offsets = Array(numRows * numCols) { i ->
            val row = i / numCols
            val col = i % numCols
            Vector2(row.toFloat(), col.toFloat())
        }

        buffer = TiledTerrainBuffer(vertices, offsets)

        transform.setScale(config.worldScale)
        transform.setTranslation(config.worldOffset)

        val camera = Resources.get<Camera>()!!

        material.apply {
            world = transform.matrix()
            view = camera.view
            viewProjection = camera.viewProjection
            heightmap = config.heightmap
            gridScale = 1.0f / config.gridSize
            minDistance = 1.0f
            maxDistance = 1500.0f
            minLOD = 1.0f
            maxLOD = 16.0f
            scaleY = config.worldScale.y
        }
        material.normalmap = owner()!!.getComponent<TerrainNormalRenderer>()!!.getMaterial().normalmap
        material.blendmap = owner()!!.getComponent<TerrainBlendRenderer>()!!.getMaterial().blendmap

        // ground textures
        val imageLoader = Resources.get<ImageLoader>()!!
        material.materialDetailMap[TiledTerrainTextureType.GRASS_TEXTURE] = TiledTerrainMaterialDetail(
            Texture2d(imageLoader.loadImage("textures/terrain/grass_01_diff.jpg")),
            Texture2d(imageLoader.loadImage("textures/terrain/grass_01_norm.jpg")),
            Texture2d(imageLoader.loadImage("textures/terrain/grass_01_disp.jpg")),
            1.0f,
            100.0f
        )

        material.materialDetailMap[TiledTerrainTextureType.DIRT_TEXTURE] = TiledTerrainMaterialDetail(
            Texture2d(imageLoader.loadImage("textures/terrain/dirt_01_diff.jpg")),
            Texture2d(imageLoader.loadImage("textures/terrain/dirt_01_norm.jpg")),
            Texture2d(imageLoader.loadImage("textures/terrain/dirt_01_disp.jpg")),
            1.0f,
            100.0f
        )

        material.materialDetailMap[TiledTerrainTextureType.ROCK_TEXTURE] = TiledTerrainMaterialDetail(
            Texture2d(imageLoader.loadImage("textures/terrain/rock_01_diff.jpg")),
            Texture2d(imageLoader.loadImage("textures/terrain/rock_01_norm.jpg")),
            Texture2d(imageLoader.loadImage("textures/terrain/rock_01_disp.jpg")),
            2.0f,
            100.0f
        )

        shader.setup()

        renderer = TiledTerrainRenderer(buffer, material, shader)
        (owner() as Object).addComponent(renderer)

        if (enablePostProcessing) {
            ppRenderer = TiledTerrainPPRenderer(buffer, material, shader)
            (owner() as Object).addComponent(ppRenderer)
        }

        (owner() as Object).addComponent(TiledTerrainGui(material))

        Events.subscribe<DrawGizmosEvent, Any>(::onDrawGizmos)

        val patchSize = Vector2(1f / config.gridSize, 1f / config.gridSize)
        val xzScale = Vector2(config.worldScale.x, config.worldScale.z)
        val xzOffset = Vector2(config.worldOffset.x, config.worldOffset.z)
        for (i in 0..<config.gridSize) {
            for (j in 0..<config.gridSize) {
                val patchLocation = Vector2(
                    i.toFloat() / config.gridSize,
                    j.toFloat() / config.gridSize
                )

                val minPoint = xzOffset + patchLocation * xzScale
                val maxPoint = minPoint + patchSize * xzScale
                val bounds = config.heightmap.calculatePatchBounds(
                    minPoint, maxPoint
                )

                val patchObject = TiledTerrainPatch()
                patchObject.getComponent<BoxAABB>()!!.setShape(bounds.shape())
                patchObject.addComponent(BoxAABBDrawer(Colors.Blue))

                (owner() as Object).addChild(patchObject)
            }
        }

        println("TILED BEHAVIOUR INITIALIZED")
    }

    override fun update(deltaTime: Float) {
        val camera = Resources.get<Camera>()!!

        material.apply {
            world = transform.matrix()
            view = camera.view
            viewProjection = camera.viewProjection
        }
        material.lightViewProjection = lightViewProjection
        material.shadowmap = Resources.get<ShadowFrameBuffer>()!!.getDepthMap()
    }

    override fun destroy() {
        Events.unsubscribe<DrawGizmosEvent, Any>(::onDrawGizmos)

        (owner() as Object)
            .getChildren()
            .filterIsInstance<TiledTerrainPatch>()
            .forEach { patch ->
                patch.getComponent<BoxAABBDrawer>()?.dispose()
            }

        material.materialDetailMap.values.forEach {
            it.diffuseMap.destroy()
            it.displacementMap.destroy()
            it.normalMap.destroy()
        }
        shader.destroy()
        buffer.destroy()
    }

    private fun buildVertices(): Array<Vector2> {
        return arrayOf(
            Vector2(0f, 0f),
            Vector2(0f, 1f),
            Vector2(1f, 0f),
            Vector2(1f, 1f)
        )
    }

    private fun onDrawGizmos(event: DrawGizmosEvent, sender: Any) {
        (owner() as Object)
            .getChildren()
            .filterIsInstance<TiledTerrainPatch>()
            .forEach { patch ->
                patch.getComponent<BoxAABBDrawer>()?.draw()
            }
    }
}