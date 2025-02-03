package platform.services.input

import core.events.Events
import graphics.gui.GuiWrapper
import org.lwjgl.glfw.GLFW

class KeyboardInput(private val window: Long) {

    private val keys = mutableMapOf<Int, Boolean>()

    private var guiWrapper: GuiWrapper? = null

    fun setGuiWrapper(guiWrapper: GuiWrapper?) {
        this.guiWrapper = guiWrapper
    }

    fun getGuiWrapper(): GuiWrapper? = guiWrapper

    fun keyCallback(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        if (guiWrapper?.wantCaptureKeyboard() == true) {
            return
        }

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