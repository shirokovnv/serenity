package platform.components.input

import core.ecs.Component
import org.lwjgl.glfw.GLFW

class KeyboardInput(private val window: Long) : Component() {
    fun keyCallback(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        if(action== GLFW.GLFW_PRESS) {
            // default action
        }
    }

    fun isKeyPressed(key: Int): Boolean {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS
    }
}