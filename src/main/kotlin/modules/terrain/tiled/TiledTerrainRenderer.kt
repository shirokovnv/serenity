package modules.terrain.tiled

import core.ecs.Behaviour
import core.math.Vector2
import core.scene.Object
import core.scene.Transform
import core.scene.camera.Camera
import graphics.assets.surface.bind
import graphics.assets.texture.Texture2d
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import modules.terrain.TerrainBlendRenderer
import modules.terrain.TerrainNormalRenderer
import platform.services.filesystem.ImageLoader

class TiledTerrainRenderer(private val config: TiledTerrainConfig) : Behaviour(), Renderer {
    private lateinit var material: TiledTerrainMaterial
    private lateinit var shader: TiledTerrainShader

    private lateinit var buffer: TiledTerrainBuffer

    private val transform: Transform
        get() = owner()!!.getComponent<Transform>()!!

    override fun create() {
        material = TiledTerrainMaterial()
        shader = TiledTerrainShader()
        shader bind material
        shader.setup()

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
        material.grassTexture = Texture2d(imageLoader.loadImage("textures/terrain/grass_01.jpg"))
        material.dirtTexture = Texture2d(imageLoader.loadImage("textures/terrain/dirt_01.jpg"))
        material.rockTexture = Texture2d(imageLoader.loadImage("textures/terrain/rock_01.jpg"))

        material.grassTexture.bind()
        material.grassTexture.bilinearFilter()

        material.dirtTexture.bind()
        material.dirtTexture.bilinearFilter()

        material.rockTexture.bind()
        material.rockTexture.bilinearFilter()

        println("TILED BEHAVIOUR INITIALIZED")
    }

    override fun update(deltaTime: Float) {
        val camera = Object.services.getService<Camera>()!!

        material.apply {
            world = transform.matrix()
            view = camera.view
            viewProjection = camera.viewProjection
        }
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