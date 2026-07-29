package modules.terrain.quadtree.top_view

import core.ecs.BaseComponent
import graphics.assets.buffer.Fbo
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass
import modules.terrain.quadtree.QuadTreeBuffer
import org.lwjgl.opengl.GL43

class QuadTreeTopViewRenderer(
    private val buffer: QuadTreeBuffer,
    private val fbo: Fbo,
    private val material: QuadTreeTopViewMaterial,
    private val shader: QuadTreeTopViewShader
): BaseComponent(), Renderer {
    override fun render(pass: RenderPass) {
        fbo.bind()
        val prevViewport = IntArray(4)
        GL43.glGetIntegerv(GL43.GL_VIEWPORT, prevViewport)

        GL43.glViewport(0, 0, fbo.getWidth(), fbo.getHeight())
        GL43.glClearColor(1f, 1f, 1f, 1f)
        GL43.glClear(GL43.GL_COLOR_BUFFER_BIT)

        shader.bind()
        shader.updateUniforms()
        buffer.bind()
        buffer.primitiveType = QuadTreeBuffer.PrimitiveType.TRIANGLE
        buffer.draw()
        buffer.unbind()
        shader.unbind()
        fbo.unbind()

        GL43.glViewport(prevViewport[0], prevViewport[1], prevViewport[2], prevViewport[3])
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}