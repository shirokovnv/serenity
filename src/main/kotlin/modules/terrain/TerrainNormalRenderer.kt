package modules.terrain

import core.ecs.Behaviour
import graphics.assets.surface.bind
import graphics.assets.texture.Texture2d
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import modules.terrain.heightmap.Heightmap
import org.lwjgl.opengl.*
import kotlin.math.ln

class TerrainNormalRenderer(
    private val heightmap: Heightmap,
    private val normalStrength: Float = 60f
) : Behaviour(), Renderer {
    private var isFirstFrame: Boolean = true
    private lateinit var shader: TerrainNormalShader
    private lateinit var material: TerrainNormalMaterial

    private val width: Int
        get() = heightmap.getTexture().getWidth()

    private val height: Int
        get() = heightmap.getTexture().getHeight()

    fun getMaterial(): TerrainNormalMaterial = material

    override fun create() {
        material = TerrainNormalMaterial()
        shader = TerrainNormalShader()
        shader bind material

        material.heightmap = heightmap
        material.normalStrength = normalStrength
        material.normalmap = Texture2d(width, height)
        material.normalmap.bind()

        GL42.glTexStorage2D(
            GL11.GL_TEXTURE_2D,
            (ln(width.toDouble()) / ln(2.0)).toInt(),
            GL30.GL_RGBA32F,
            width,
            height
        )
        material.normalmap.bilinearFilter()
        shader.setup()

        println("NORMAL MAP RENDER BEHAVIOUR INITIALIZED")
    }

    override fun update(deltaTime: Float) {
    }

    override fun destroy() {
    }

    override fun render(pass: RenderPass) {
        if (isFirstFrame) {
            shader.bind()
            shader.updateUniforms()

            GL42.glBindImageTexture(0, material.normalmap.getId(), 0, false, 0, GL15.GL_WRITE_ONLY, GL30.GL_RGBA32F)
            GL43.glDispatchCompute(width / 16, height / 16, 1)

            val error = GL43.glGetError()
            if (error != GL43.GL_NO_ERROR){
                println("Error while executing compute shader: $error")
            }

            GL43.glFinish()
            material.normalmap.bind()
            material.normalmap.bilinearFilter()
            shader.unbind()

            isFirstFrame = false

            println("NORMAL MAP COMPUTED")
        }
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}