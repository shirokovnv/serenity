package platform.services.input

import org.lwjgl.glfw.GLFW

class KeyboardInput(private val window: Long) {

    private val listeners = mutableListOf<KeyboardInputListener>()
    private val keys = mutableMapOf<Int, Boolean>()

    fun addListener(listener: KeyboardInputListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: KeyboardInputListener) {
        listeners.remove(listener)
    }

    fun keyCallback(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        when (action) {
            GLFW.GLFW_PRESS -> {
                keys[key] = true
                listeners.forEach{it.onKeyPressed(key)}
            }

            GLFW.GLFW_RELEASE ->{
                keys[key] = false
                listeners.forEach{it.onKeyReleased(key)}
            }
        }
    }

    fun isKeyPressed(key: Int): Boolean {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS
    }

    fun isKeyHolding(key: Int): Boolean = keys[key] ?: false
}