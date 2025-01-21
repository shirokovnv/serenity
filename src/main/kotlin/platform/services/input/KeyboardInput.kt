package platform.services.input

import core.event.Events
import org.lwjgl.glfw.GLFW

class KeyboardInput(private val window: Long) {

    private val keys = mutableMapOf<Int, Boolean>()

    fun keyCallback(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        when (action) {
            GLFW.GLFW_PRESS -> {
                Events.publish(KeyPressedEvent(key), this)
                keys[key] = true
            }

            GLFW.GLFW_RELEASE ->{
                Events.publish(KeyReleasedEvent(key), this)
                keys[key] = false
            }
        }
    }

    fun isKeyPressed(key: Int): Boolean {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS
    }

    fun isKeyHolding(key: Int): Boolean = keys[key] ?: false
}