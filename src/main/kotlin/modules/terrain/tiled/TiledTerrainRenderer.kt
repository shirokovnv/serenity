package modules.terrain.tiled

import core.ecs.BaseComponent
import core.math.Quaternion
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.ReflectionPass
import graphics.rendering.passes.RefractionPass
import graphics.rendering.passes.RenderPass
import modules.water.plane.WaterPlaneConstants
import org.lwjgl.opengl.GL20

class TiledTerrainRenderer(
    private val buffer: TiledTerrainBuffer,
    private val material: TiledTerrainMaterial,
    private val shader: TiledTerrainShader
) : BaseComponent(), Renderer {
    companion object {
        private val supportedPasses = listOf(NormalPass, ReflectionPass, RefractionPass)
    }

    override fun render(pass: RenderPass) {
        GL20.glEnable(GL20.GL_CULL_FACE)
        GL20.glCullFace(GL20.GL_BACK)

        material.renderInBlack = false

        material.clipPlane = when(pass) {
            RefractionPass -> Quaternion(0f, -1f, 0f, WaterPlaneConstants.DEFAULT_WORLD_HEIGHT + 1f)
            ReflectionPass -> Quaternion(0f, 1f, 0f, -WaterPlaneConstants.DEFAULT_WORLD_HEIGHT + 1f)
            else -> Quaternion(0f, -1f, 0f, 10000f)
        }

        shader.bind()
        shader.updateUniforms()
        buffer.draw()
        shader.unbind()

        GL20.glDisable(GL20.GL_CULL_FACE)
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass in supportedPasses
    }
}