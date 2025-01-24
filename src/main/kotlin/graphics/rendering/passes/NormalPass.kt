package graphics.rendering.passes

import core.events.Disposable
import core.events.Events
import core.management.Resources
import graphics.assets.buffer.DepthBufferType
import graphics.assets.buffer.Fbo
import graphics.assets.texture.Texture2d
import graphics.rendering.viewport.ViewportInterface
import org.lwjgl.opengl.GL43.*
import platform.services.input.WindowResizedEvent

object NormalPass : RenderPass, Disposable {
    override val name = "NORMAL_PASS"

    private var viewport: ViewportInterface = Resources.get<ViewportInterface>()!!
    private var fbo: Fbo = Fbo(viewport.getWidth(), viewport.getHeight(), DepthBufferType.DEPTH_RENDER_BUFFER)

    private var width: Int = viewport.getWidth()
    private var height: Int = viewport.getHeight()

    init {
        Events.subscribe<WindowResizedEvent, Any>(::onWindowResize)
    }

    override fun start() {
        fbo.bind()
        glViewport(0, 0, width, height)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    override fun finish() {
        fbo.unbind()
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
    }
}