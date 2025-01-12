package modules.input

import core.ecs.Behaviour
import core.scene.Object
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import platform.services.input.KeyboardInput
import platform.services.input.KeyboardInputListener

class InputController: Behaviour(), KeyboardInputListener {
    private var isWireframe: Boolean = false

    override fun create() {
        Object.services.getService<KeyboardInput>()!!.addListener(this)
    }

    override fun update(deltaTime: Float) {
    }

    override fun destroy() {
    }

    override fun onKeyPressed(key: Int) {
        when(key) {
            GLFW.GLFW_KEY_5 -> toggleWireframeMode()
        }
    }

    override fun onKeyReleased(key: Int) {
    }

    private fun toggleWireframeMode() {
        if (isWireframe) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
        } else {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
        }

        isWireframe = !isWireframe
    }
}