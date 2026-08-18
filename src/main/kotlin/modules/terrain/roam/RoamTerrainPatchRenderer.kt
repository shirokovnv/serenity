package modules.terrain.roam

import core.ecs.BaseComponent
import core.management.Resources
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import modules.terrain.roam.buffers.PatchBufferInterface
import modules.terrain.roam.buffers.PatchSsbo

class RoamTerrainPatchRenderer(
    private val buffer: PatchBufferInterface,
    private val shader: RoamTerrainPatchShader,
    private val material: RoamTerrainPatchMaterial,
    private val metrics: RoamTerrainPatchMetrics
) : BaseComponent(), Renderer {
    override fun render(pass: RenderPass) {
        val ssbo = Resources.get<PatchSsbo>()

        val startTime = System.nanoTime()
        ssbo?.setBindingPoint(1)
        ssbo?.bind()
        shader.bind()
        shader.updateUniforms()
        buffer.bind()
        buffer.draw()
        buffer.unbind()
        shader.unbind()
        ssbo?.unbind()
        val endTime = System.nanoTime()
        val elapsedTime = endTime - startTime
        metrics.drawTimeMs = elapsedTime / 1000000
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}