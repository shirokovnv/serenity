package platform.components.input

import core.ecs.Component
import org.lwjgl.glfw.GLFW

class KeyboardInput(private val window: Long): Component() {

    private val listeners = mutableListOf<KeyboardInputListener>()

    fun addListener(listener: KeyboardInputListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: KeyboardInputListener) {
        listeners.remove(listener)
    }

    fun keyCallback(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        when (action) {
            GLFW.GLFW_PRESS -> {
                listeners.forEach{it.onKeyPressed(key)}
            }

            GLFW.GLFW_RELEASE ->{
                listeners.forEach{it.onKeyReleased(key)}
            }
        }
    }

    fun isKeyPressed(key: Int): Boolean {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS
    }
}