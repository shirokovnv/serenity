package modules.terrain.tiled

import core.ecs.Behaviour
import core.math.Matrix4
import core.math.Vector2
import core.scene.Object
import core.scene.Transform
import core.scene.camera.Camera
import core.scene.camera.OrthographicCamera
import graphics.assets.surface.bind
import graphics.assets.texture.Texture2d
import graphics.rendering.Renderer
import graphics.rendering.fbo.ShadowFrameBuffer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import modules.light.SunLightManager
import modules.terrain.TerrainBlendRenderer
import modules.terrain.TerrainNormalRenderer
import platform.services.filesystem.ImageLoader

class TiledTerrainRenderer(private val config: TiledTerrainConfig) : Behaviour(), Renderer {
    private lateinit var material: TiledTerrainMaterial
    private lateinit var shader: TiledTerrainShader

    private lateinit var buffer: TiledTerrainBuffer

    private val transform: Transform
        get() = owner()!!.getComponent<Transform>()!!

    private val lightViewProjection: Matrix4
        get() {
            val sunLightManager = Object.services.getService<SunLightManager>()!!
            val orthographicCamera = Object.services.getService<OrthographicCamera>()!!

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

        val camera = Object.services.getService<Camera>()!!

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
        val imageLoader = Object.services.getService<ImageLoader>()!!
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

        println("TILED BEHAVIOUR INITIALIZED")
    }

    override fun update(deltaTime: Float) {
        val camera = Object.services.getService<Camera>()!!

        material.apply {
            world = transform.matrix()
            view = camera.view
            viewProjection = camera.viewProjection
        }
        material.lightViewProjection = lightViewProjection
        material.shadowmap = Object.services.getService<ShadowFrameBuffer>()!!.getDepthMap()
    }

    override fun destroy() {
        buffer.destroy()
    }

    override fun render(pass: RenderPass) {
        shader.bind()
        shader.updateUniforms()
        buffer.draw()
        shader.unbind()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }

    private fun buildVertices(): Array<Vector2> {
        return arrayOf(
            Vector2(0f, 0f),
            Vector2(0f, 1f),
            Vector2(1f, 0f),
            Vector2(1f, 1f)
        )
    }
}