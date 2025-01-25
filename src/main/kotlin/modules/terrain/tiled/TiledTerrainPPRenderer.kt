package modules.terrain.tiled

import core.ecs.BaseComponent
import graphics.rendering.Renderer
import graphics.rendering.passes.PostProcPass
import graphics.rendering.passes.RenderPass
import org.lwjgl.opengl.GL20

class TiledTerrainPPRenderer(
    private val buffer: TiledTerrainBuffer,
    private val material: TiledTerrainMaterial,
    private val shader: TiledTerrainShader
) : BaseComponent(), Renderer {
    override fun render(pass: RenderPass) {
        GL20.glEnable(GL20.GL_CULL_FACE)
        GL20.glCullFace(GL20.GL_BACK)

        material.renderInBlack = true

        shader.bind()
        shader.updateUniforms()
        buffer.draw()
        shader.unbind()

        GL20.glDisable(GL20.GL_CULL_FACE)
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == PostProcPass
    }
}