package graphics.rendering.viewport

import core.events.Events
import core.events.SubscriberInterface
import platform.services.input.WindowResizedEvent

class Viewport(private var width: Int, private var height: Int): ViewportInterface, SubscriberInterface {

    private var hasSubscription: Boolean = false

    init {
        subscribe()
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

    override fun subscribe() {
        if (!hasSubscription) {
            Events.subscribe<WindowResizedEvent, Any>(::onWindowResized)
            hasSubscription = true
        }
    }

    override fun unsubscribe() {
        if (hasSubscription) {
            Events.unsubscribe<WindowResizedEvent, Any>(::onWindowResized)
            hasSubscription = false
        }
    }
}