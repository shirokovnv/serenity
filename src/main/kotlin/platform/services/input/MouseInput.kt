package platform.services.input

import core.events.Events
import graphics.gui.GuiWrapper
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

class MouseInput(val window: Long) {
    private var mouseXDelta = 0f
    private var mouseYDelta = 0f
    private var mouseScrollY = 0f

    private var lastX = 0.0
    private var lastY = 0.0
    private var isFirstMouse = true

    private var guiWrapper: GuiWrapper? = null

    private val pressedButtons = mutableSetOf<Int>()

    fun setGuiWrapper(guiWrapper: GuiWrapper?) {
        this.guiWrapper = guiWrapper
    }

    fun getGuiWrapper(): GuiWrapper? = guiWrapper

    fun mousePosCallback(window: Long, xpos: Double, ypos: Double) {

        if (guiWrapper?.wantCaptureMouse() == true) {
            return
        }

        if (isFirstMouse) {
            lastX = xpos
            lastY = ypos
            isFirstMouse = false
        } else { // Only update if not the first mouse movement
            mouseXDelta = (xpos - lastX).toFloat()
            mouseYDelta = (lastY - ypos).toFloat()
            Events.publish(MouseMovedEvent(mouseXDelta, mouseYDelta), this)
        }

        lastX = xpos
        lastY = ypos // Update position for the next frame
    }

    fun mouseScrollCallback(window: Long, xoffset: Double, yoffset: Double) {
        mouseScrollY = yoffset.toFloat()
        Events.publish(MouseScrolledEvent(mouseScrollY), this)
    }

    fun mouseButtonCallback(window: Long, button: Int, action: Int, mods: Int) {
        if (action == GLFW_PRESS) {
            pressedButtons.add(button)
            Events.publish(MouseButtonPressedEvent(button), this)
        } else if (action == GLFW_RELEASE) {
            pressedButtons.remove(button)
            Events.publish(MouseButtonReleasedEvent(button), this)
        }
    }

    fun isMouseButtonHolding(button: Int): Boolean {
        return pressedButtons.contains(button)
    }

    fun resetMouse(){
        isFirstMouse = true
    }

    fun lastX(): Double = lastX
    fun lastY(): Double = lastY
}