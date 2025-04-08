package graphics.rendering.passes

import org.lwjgl.opengl.GL43.*

object ReflectionPass : BaseRenderPass() {
    override val name = "REFLECTION_PASS"

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
}