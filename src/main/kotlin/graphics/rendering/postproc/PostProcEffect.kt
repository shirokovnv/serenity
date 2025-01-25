package graphics.rendering.postproc

import core.events.Events
import core.management.Disposable
import core.management.Resources
import graphics.assets.buffer.Fbo
import graphics.assets.texture.Texture2d
import graphics.rendering.viewport.ViewportInterface
import platform.services.input.WindowResizedEvent

abstract class PostProcEffect: Disposable {
    protected val screenQuad: ScreenQuad
        get() = defaultScreenQuadProvider()

    var viewport: ViewportInterface = Resources.get<ViewportInterface>()!!
    abstract var fbo: Fbo?

    init {
        Events.subscribe<WindowResizedEvent, Any>(::onWindowResize)
    }

    abstract fun render(inputImage: Texture2d)
    open fun getOutputImage(): Texture2d {
        return fbo?.getColorTexture() ?: Texture2d(viewport.getWidth(), viewport.getHeight())
    }

    protected open fun onWindowResize(event: WindowResizedEvent, sender: Any) {
        if (fbo != null) {
            fbo!!.destroy()
            fbo!!.setWidth(event.newWidth)
            fbo!!.setHeight(event.newHeight)
            fbo!!.create()
        }
    }

    override fun dispose() {
        Events.unsubscribe<WindowResizedEvent, Any>(::onWindowResize)
    }
}