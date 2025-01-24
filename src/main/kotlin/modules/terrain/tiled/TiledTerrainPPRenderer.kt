package modules.terrain.tiled

import core.ecs.BaseComponent
import graphics.rendering.Renderer
import graphics.rendering.passes.PostProcPass
import graphics.rendering.passes.RenderPass

class TiledTerrainPPRenderer(
    private val buffer: TiledTerrainBuffer,
    private val material: TiledTerrainMaterial,
    private val shader: TiledTerrainShader
) : BaseComponent(), Renderer {
    override fun render(pass: RenderPass) {
        material.renderInBlack = true

        shader.bind()
        shader.updateUniforms()
        buffer.draw()
        shader.unbind()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == PostProcPass
    }
}