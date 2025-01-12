package modules.terrain

import core.ecs.Behaviour
import graphics.assets.surface.bind
import graphics.assets.texture.Texture2d
import graphics.assets.texture.texture2dPrintDataCallback
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import org.lwjgl.opengl.*
import kotlin.math.ln

class TerrainBlendRenderer(
    private var heightmap: Heightmap,
    private var elevationData: Array<ElevationData>
) : Behaviour(), Renderer {

    private var isFirstFrame: Boolean = true
    private lateinit var shader: TerrainBlendShader
    private lateinit var material: TerrainBlendMaterial

    private val width: Int
        get() = heightmap.getTexture().getWidth()

    private val height: Int
        get() = heightmap.getTexture().getHeight()

    fun getMaterial(): TerrainBlendMaterial = material

    override fun create() {
        material = TerrainBlendMaterial()
        shader = TerrainBlendShader()
        shader bind material

        material.elevationData = elevationData
        material.heightmap = heightmap
        material.normalmap = owner()!!.getComponent<TerrainNormalRenderer>()!!.getMaterial().normalmap
        material.blendmap = Texture2d(width, height)
        material.blendmap.bind()

        GL42.glTexStorage2D(
            GL11.GL_TEXTURE_2D,
            (ln(width.toDouble()) / ln(2.0)).toInt(),
            GL30.GL_RGBA32F,
            width,
            height
        )

        material.blendmap.bilinearFilter()
        shader.setup()

        println("BLEND MAP RENDER BEHAVIOUR INITIALIZED")
    }

    override fun update(deltaTime: Float) {
    }

    override fun destroy() {
    }

    override fun render(pass: RenderPass) {
        if (isFirstFrame) {
            shader.bind()
            shader.updateUniforms()

            GL42.glBindImageTexture(0, material.blendmap.getId(), 0, false, 0, GL15.GL_WRITE_ONLY, GL30.GL_RGBA32F)
            GL43.glDispatchCompute(width / 16, height / 16, 1)

            val error = GL43.glGetError()
            if (error != GL43.GL_NO_ERROR){
                println("Error while executing compute shader: $error")
            }

            GL43.glFinish()
            material.blendmap.bind()
            material.blendmap.bilinearFilter()
            shader.unbind()

            isFirstFrame = false

            println("BLEND MAP COMPUTED")
        }
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}