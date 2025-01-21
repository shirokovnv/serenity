package platform.services.input

import core.event.Events

class WindowInput(private val window: Long) {
    fun windowResizeCallback(window: Long, newWidth: Int, newHeight: Int){
        Events.publish(WindowResizedEvent(newWidth, newHeight), this)
    }
}