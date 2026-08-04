package modules.terrain.roam

import core.ecs.BaseComponent
import core.scene.Object
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import modules.terrain.roam.buffers.PatchBufferInterface

class RoamTerrainPatchRenderer(
    private val buffer: PatchBufferInterface,
    private val shader: RoamTerrainPatchShader,
    private val material: RoamTerrainPatchMaterial,
    private val metrics: RoamTerrainPatchMetrics
) : BaseComponent(), Renderer {
    override fun render(pass: RenderPass) {
        val startTime = System.nanoTime()
        shader.bind()
        shader.updateUniforms()
        buffer.bind()
        buffer.draw()
        buffer.unbind()
        shader.unbind()
        val endTime = System.nanoTime()
        val elapsedTime = endTime - startTime
        metrics.drawTimeMs = elapsedTime / 1000000
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}