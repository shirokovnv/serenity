package modules.light

import core.ecs.Behaviour
import core.scene.Object
import org.lwjgl.glfw.GLFW
import platform.services.input.KeyboardInput
import platform.services.input.KeyboardInputListener

class SunLightController(private val moveSpeed: Float = 0.1f): Behaviour(), KeyboardInputListener {
    private lateinit var sunLightManager: SunLightManager

    override fun create() {
        sunLightManager = SunLightManager()

        Object.services.putService<SunLightManager>(sunLightManager)
        Object.services.getService<KeyboardInput>()!!.addListener(this)
    }

    override fun update(deltaTime: Float) {
        sunLightManager.update()
    }

    override fun destroy() {
    }

    override fun onKeyPressed(key: Int) {
        when(key) {
            GLFW.GLFW_KEY_EQUAL -> sunLightManager.setTimeOfDay(sunLightManager.timeOfDay() + moveSpeed)
            GLFW.GLFW_KEY_MINUS -> sunLightManager.setTimeOfDay(sunLightManager.timeOfDay() - moveSpeed)
        }
    }

    override fun onKeyReleased(key: Int) {
    }
}