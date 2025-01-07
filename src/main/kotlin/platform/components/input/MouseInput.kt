package platform.components.input

import core.ecs.Component
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

class MouseInput(val window: Long): Component() {
    private var mouseXDelta = 0f
    private var mouseYDelta = 0f
    private var mouseScrollY = 0f

    private var lastX = 0.0
    private var lastY = 0.0
    private var isFirstMouse = true

    private val listeners = mutableListOf<MouseInputListener>()

    fun addListener(listener: MouseInputListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: MouseInputListener) {
        listeners.remove(listener)
    }

    fun mousePosCallback(window: Long, xpos: Double, ypos: Double) {

        if (isFirstMouse) {
            lastX = xpos
            lastY = ypos
            isFirstMouse = false
        } else { // Only update if not the first mouse movement
            mouseXDelta = (xpos - lastX).toFloat()
            mouseYDelta = (lastY - ypos).toFloat()
            listeners.forEach{ it.onMouseMoved(mouseXDelta, mouseYDelta)}
        }

        lastX = xpos
        lastY = ypos // Update position for the next frame
    }

    fun mouseScrollCallback(window: Long, xoffset: Double, yoffset: Double) {
        mouseScrollY = yoffset.toFloat()
        listeners.forEach{ it.onMouseScrolled(mouseScrollY) }
    }

    fun mouseButtonCallback(window: Long, button: Int, action: Int, mods: Int) {
        if (action == GLFW_PRESS) {
            listeners.forEach { it.onMouseButtonPressed(button) }
        } else if (action == GLFW_RELEASE) {
            listeners.forEach { it.onMouseButtonReleased(button) }
        }
    }

    fun resetMouse(){
        isFirstMouse = true
    }
}