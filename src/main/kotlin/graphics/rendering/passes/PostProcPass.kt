package graphics.rendering.passes

import core.events.Events
import core.management.Disposable
import core.management.Resources
import graphics.assets.buffer.DepthBufferType
import graphics.assets.buffer.Fbo
import graphics.assets.texture.Texture2d
import graphics.rendering.postproc.PostProcessor
import graphics.rendering.viewport.ViewportInterface
import org.lwjgl.opengl.GL43
import platform.services.input.WindowResizedEvent

object PostProcPass : RenderPass, Disposable {
    override val name = "POST_PROCESSING_PASS"

    private var viewport: ViewportInterface = Resources.get<ViewportInterface>()!!
    private var fbo: Fbo = Fbo(viewport.getWidth(), viewport.getHeight(), DepthBufferType.DEPTH_RENDER_BUFFER)

    private var width: Int = viewport.getWidth()
    private var height: Int = viewport.getHeight()

    init {
        Events.subscribe<WindowResizedEvent, Any>(::onWindowResize)
    }

    override fun start() {
        if (PostProcessor.countEffects() == 0) {
            return
        }

        fbo.bind()
        GL43.glViewport(0, 0, width, height)
        GL43.glClear(GL43.GL_COLOR_BUFFER_BIT or GL43.GL_DEPTH_BUFFER_BIT)
    }

    override fun finish() {
        if (PostProcessor.countEffects() == 0) {
            return
        }
        fbo.unbind()

        PostProcessor.process(NormalPass.getColorTexture())
    }

    fun getColorTexture(): Texture2d = fbo.getColorTexture()

    private fun onWindowResize(event: WindowResizedEvent, sender: Any) {
        width = event.newWidth
        height = event.newHeight

        fbo.destroy()
        fbo.setWidth(width)
        fbo.setHeight(height)
        fbo.create()
    }

    override fun dispose() {
        Events.unsubscribe<WindowResizedEvent, Any>(::onWindowResize)

        fbo.destroy()
    }
}