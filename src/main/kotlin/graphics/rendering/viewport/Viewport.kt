package graphics.rendering.viewport

import core.events.Events
import core.events.Disposable
import platform.services.input.WindowResizedEvent

class Viewport(private var width: Int, private var height: Int): ViewportInterface, Disposable {

    init {
        Events.subscribe<WindowResizedEvent, Any>(::onWindowResized)
    }

    override fun getWidth(): Int {
        return width
    }

    override fun getHeight(): Int {
        return height
    }

    private fun onWindowResized(event: WindowResizedEvent, sender: Any) {
        width = event.newWidth
        height = event.newHeight
    }

    override fun dispose() {
        Events.unsubscribe<WindowResizedEvent, Any>(::onWindowResized)
    }
}