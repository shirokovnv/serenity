package graphics.rendering.passes

import graphics.assets.buffer.DepthBufferType
import graphics.assets.buffer.Fbo
import org.lwjgl.opengl.GL43.*

object RefractionPass : BaseRenderPass() {
    override val name = "REFRACTION_PASS"

    override fun onStart() {
        fbo.bind()

        glClearColor(0f, 0f, 0f, 0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glEnable(GL_CLIP_DISTANCE0)
    }

    override fun onFinish() {
        fbo.unbind()

        glDisable(GL_CLIP_DISTANCE0)
    }

    override fun initFbo(): Fbo {
        return Fbo(viewport.getWidth(), viewport.getHeight(), DepthBufferType.DEPTH_TEXTURE)
    }
}