package graphics.rendering.context

import core.ecs.Behaviour
import core.events.Events
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import platform.services.input.KeyPressedEvent

class RenderContextController: Behaviour() {
    override fun create() {
        Events.subscribe<KeyPressedEvent, Any>(::onKeyPressed)
    }

    override fun update(deltaTime: Float) {
    }

    override fun destroy() {
        Events.unsubscribe<KeyPressedEvent, Any>(::onKeyPressed)
    }

    private fun onKeyPressed(event: KeyPressedEvent, sender: Any) {
        val key = event.key

        when (key) {
            GLFW.GLFW_KEY_4 -> toggleOnDrawGizmos()
            GLFW.GLFW_KEY_5 -> toggleOnDrawWireframe()
        }
    }

    private fun toggleOnDrawWireframe() {
        if (RenderContext.onDrawWireframe) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
        } else {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
        }

        RenderContext.onDrawWireframe = !RenderContext.onDrawWireframe
    }

    private fun toggleOnDrawGizmos() {
        RenderContext.onDrawGizmos = !RenderContext.onDrawGizmos
    }
}