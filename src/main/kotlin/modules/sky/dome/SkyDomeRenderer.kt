package modules.sky.dome

import core.ecs.BaseComponent
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import modules.light.AtmosphereConstantsSsbo

class SkyDomeRenderer(
    private val buffer: SkyDomeBuffer,
    private val material: SkyDomeMaterial,
    private val shader: SkyDomeShader,
    private val atmosphereConstantsSsbo: AtmosphereConstantsSsbo
): BaseComponent(), Renderer {
    override fun render(pass: RenderPass) {
        shader.bind()
        shader.updateUniforms()
        atmosphereConstantsSsbo.setBindingPoint(0)
        atmosphereConstantsSsbo.bind()
        buffer.draw()
        atmosphereConstantsSsbo.unbind()
        shader.unbind()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}