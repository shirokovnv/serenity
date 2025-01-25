package graphics.rendering.passes

import core.events.Events
import core.management.Disposable
import core.management.Resources
import graphics.assets.buffer.DepthBufferType
import graphics.assets.buffer.Fbo
import graphics.assets.texture.Texture2d
import graphics.rendering.viewport.ViewportInterface
import platform.services.input.WindowResizedEvent

abstract class BaseRenderPass : RenderPass, Disposable {
    protected var viewport: ViewportInterface = Resources.get<ViewportInterface>()!!
    protected var fbo: Fbo = Fbo(viewport.getWidth(), viewport.getHeight(), DepthBufferType.DEPTH_RENDER_BUFFER)

    protected var width: Int = viewport.getWidth()
    protected var height: Int = viewport.getHeight()
    private var isResized: Boolean = false

    init {
        Events.subscribe<WindowResizedEvent, Any>(::onWindowResize)
    }

    protected open fun onWindowResize(event: WindowResizedEvent, sender: Any) {
        width = event.newWidth
        height = event.newHeight
        isResized = true
    }

    override fun start() {
        processResizing()
        onStart()
    }

    override fun finish() {
        onFinish()
    }

    override fun dispose() {
        Events.unsubscribe<WindowResizedEvent, Any>(NormalPass::onWindowResize)

        fbo.destroy()
    }

    fun getColorTexture(): Texture2d = fbo.getColorTexture()

    abstract fun onStart()
    abstract fun onFinish()

    private fun processResizing() {
        if (isResized) {
            fbo.destroy()
            fbo.setWidth(width)
            fbo.setHeight(height)
            fbo.create()

            isResized = false
        }
    }
}