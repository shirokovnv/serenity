package graphics.rendering.passes

import core.events.Events
import core.management.Disposable
import graphics.rendering.postproc.PostProcessor
import org.lwjgl.opengl.GL43
import platform.services.input.WindowResizedEvent

object PostProcPass : BaseRenderPass(), Disposable {
    override val name = "POST_PROCESSING_PASS"

    init {
        Events.subscribe<WindowResizedEvent, Any>(::onWindowResize)
    }

    override fun onStart() {
        if (PostProcessor.countEffects() == 0) {
            return
        }

        fbo.bind()
        GL43.glViewport(0, 0, width, height)
        GL43.glClear(GL43.GL_COLOR_BUFFER_BIT or GL43.GL_DEPTH_BUFFER_BIT)
    }

    override fun onFinish() {
        if (PostProcessor.countEffects() == 0) {
            return
        }
        fbo.unbind()

        PostProcessor.process(NormalPass.getColorTexture())
    }
}