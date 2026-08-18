package modules.terrain.roam.gizmos

import core.ecs.BaseComponent
import core.management.Resources
import graphics.rendering.Drawable
import modules.terrain.roam.buffers.PatchBufferInterface
import modules.terrain.roam.buffers.PatchSsbo

class RoamPatchBoundsDrawer(
    private val buffer: PatchBufferInterface,
    private val shader: RoamPatchBoundsShader,
    private val material: RoamPatchBoundsMaterial
): BaseComponent(), Drawable {
    override fun draw() {
        val ssbo = Resources.get<PatchSsbo>()

        ssbo?.setBindingPoint(1)
        ssbo?.bind()
        shader.bind()
        shader.updateUniforms()
        buffer.bind()
        buffer.draw()
        buffer.unbind()
        shader.unbind()
        ssbo?.unbind()
    }
}