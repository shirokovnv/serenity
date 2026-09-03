package modules.terrain.roam.top_view

import core.ecs.BaseComponent
import core.management.Resources
import graphics.assets.buffer.Fbo
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import modules.terrain.roam.RoamTerrainPatch
import modules.terrain.roam.buffers.PatchBufferInterface
import modules.terrain.roam.buffers.PatchSsbo
import org.lwjgl.opengl.GL43

class RoamTopViewRenderer(
    private val patch: RoamTerrainPatch,
    private val fbo: Fbo,
    private val material: RoamTopViewMaterial,
    private val shader: RoamTopViewShader
) : BaseComponent(), Renderer {
    private val buffer: PatchBufferInterface
        get() = patch.buffer()

    override fun render(pass: RenderPass) {
        fbo.bind()
        val prevViewport = IntArray(4)
        GL43.glGetIntegerv(GL43.GL_VIEWPORT, prevViewport)

        GL43.glViewport(0, 0, fbo.getWidth(), fbo.getHeight())
        GL43.glClearColor(1f, 1f, 1f, 1f)
        GL43.glClear(GL43.GL_COLOR_BUFFER_BIT)

        val ssbo = Resources.get<PatchSsbo>()
        ssbo?.setBindingPoint(1)
        ssbo?.bind()

        shader.bind()
        shader.updateUniforms()
        buffer.bind()
        buffer.draw()
        buffer.unbind()
        shader.unbind()
        fbo.unbind()

        ssbo?.unbind()

        GL43.glViewport(prevViewport[0], prevViewport[1], prevViewport[2], prevViewport[3])
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}