package modules.light

import core.ecs.Behaviour
import core.events.Events
import core.management.Resources
import org.lwjgl.glfw.GLFW
import platform.services.input.KeyPressedEvent
import platform.services.input.KeyReleasedEvent
import platform.services.input.KeyboardInput

enum class SunMovement {
    FORWARD,
    BACKWARD
}

class SunLightController(private val moveSpeed: Float = 0.01f): Behaviour() {
    private lateinit var sunLightManager: SunLightManager
    private val movement = mutableMapOf<SunMovement, Boolean>()

    override fun create() {
        sunLightManager = SunLightManager()

        Resources.put<SunLightManager>(sunLightManager)

        Events.subscribe<KeyPressedEvent, Any>(::onKeyPressed)
        Events.subscribe<KeyReleasedEvent, Any>(::onKeyReleased)
    }

    override fun update(deltaTime: Float) {
        val keyboardInput = Resources.get<KeyboardInput>()!!
        movement[SunMovement.FORWARD] = keyboardInput.isKeyHolding(GLFW.GLFW_KEY_EQUAL)
        movement[SunMovement.BACKWARD] = keyboardInput.isKeyHolding(GLFW.GLFW_KEY_MINUS)

        movement.forEach { (direction, isPressed) ->
            if(isPressed){
                processMovement(direction)
            }
        }

        sunLightManager.update()
    }

    override fun destroy() {
        Events.unsubscribe<KeyPressedEvent, Any>(::onKeyPressed)
        Events.unsubscribe<KeyReleasedEvent, Any>(::onKeyReleased)
    }

    private fun onKeyPressed(event: KeyPressedEvent, sender: Any) {
        getDirection(event.key)?.let { direction ->
            movement[direction] = true
        }
    }

    private fun onKeyReleased(event: KeyReleasedEvent, sender: Any) {
        getDirection(event.key)?.let { direction ->
            movement[direction] = false
        }
    }

    private fun getDirection(key: Int): SunMovement? {
        return when (key) {
            GLFW.GLFW_KEY_EQUAL -> SunMovement.FORWARD
            GLFW.GLFW_KEY_MINUS -> SunMovement.BACKWARD
            else -> null
        }
    }

    private fun processMovement(direction: SunMovement) {
        when(direction) {
            SunMovement.FORWARD -> sunLightManager.setTimeOfDay(sunLightManager.timeOfDay() + moveSpeed)
            SunMovement.BACKWARD -> sunLightManager.setTimeOfDay(sunLightManager.timeOfDay() - moveSpeed)
        }
    }
}