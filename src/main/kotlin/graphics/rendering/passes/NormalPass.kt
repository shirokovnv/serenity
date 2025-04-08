package graphics.rendering.passes

import core.events.Events
import core.management.Disposable
import graphics.rendering.context.RenderContext
import graphics.rendering.postproc.PostProcessor
import org.lwjgl.opengl.GL43.*
import platform.services.input.WindowResizedEvent

object NormalPass : BaseRenderPass(), Disposable {
    override val name = "NORMAL_PASS"

    override fun onStart() {
        if (PostProcessor.countEffects() > 0) {
            fbo.bind()
        }

        glViewport(0, 0, width, height)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    override fun onFinish() {
        RenderContext.dispatchOnDrawGizmos()

        if (PostProcessor.countEffects() > 0) {
            fbo.unbind()
        }
    }

    override fun dispose() {
        Events.unsubscribe<WindowResizedEvent, Any>(::onWindowResize)

        fbo.destroy()
    }
}