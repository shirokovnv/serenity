package modules.terrain.roam

import core.ecs.BaseComponent
import core.management.Resources
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import modules.light.AtmosphereConstantsSsbo
import modules.terrain.roam.buffers.PatchBufferInterface
import modules.terrain.roam.buffers.PatchSsbo

class RoamTerrainPatchRenderer(
    private val patch: RoamTerrainPatch,
    private val shader: RoamTerrainPatchShader,
    private val material: RoamTerrainPatchMaterial,
    private val metrics: RoamTerrainPatchMetrics
) : BaseComponent(), Renderer {

    private val buffer: PatchBufferInterface
        get() = patch.buffer()

    private val patchSsbo = Resources.get<PatchSsbo>()
    private val atmosphereConstantsSsbo = Resources.get<AtmosphereConstantsSsbo>()

    override fun render(pass: RenderPass) {
        val startTime = System.nanoTime()
        atmosphereConstantsSsbo?.setBindingPoint(0)
        atmosphereConstantsSsbo?.bind()

        patchSsbo?.setBindingPoint(1)
        patchSsbo?.bind()
        shader.bind()
        shader.updateUniforms()
        buffer.bind()
        buffer.draw()
        buffer.unbind()
        shader.unbind()
        patchSsbo?.unbind()

        atmosphereConstantsSsbo?.unbind()

        val endTime = System.nanoTime()
        val elapsedTime = endTime - startTime
        metrics.drawTimeMs = elapsedTime / 1000000
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}