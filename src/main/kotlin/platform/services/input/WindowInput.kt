package platform.services.input

import core.events.Events

class WindowInput(private val window: Long) {
    fun windowResizeCallback(window: Long, newWidth: Int, newHeight: Int){
        Events.publish(WindowResizedEvent(newWidth, newHeight), this)
    }
}