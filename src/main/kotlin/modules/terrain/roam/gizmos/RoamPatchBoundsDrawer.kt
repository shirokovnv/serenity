package modules.terrain.roam.gizmos

import core.ecs.BaseComponent
import graphics.rendering.Drawable
import modules.terrain.roam.buffers.PatchBufferInterface

class RoamPatchBoundsDrawer(
    private val buffer: PatchBufferInterface,
    private val shader: RoamPatchBoundsShader,
    private val material: RoamPatchBoundsMaterial
): BaseComponent(), Drawable {
    override fun draw() {
        shader.bind()
        shader.updateUniforms()
        buffer.bind()
        buffer.draw()
        buffer.unbind()
        shader.unbind()
    }
}