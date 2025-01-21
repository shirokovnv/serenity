package core.scene.camera

import core.ecs.Behaviour
import core.event.Events
import core.management.Resources
import core.math.extensions.toRadians
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import platform.services.input.*

enum class CameraMovement {
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT,
    UP,
    DOWN
}

enum class CameraRotation {
    LEFT,
    RIGHT,
    UP,
    DOWN
}

class CameraController(
    private var moveSpeed: Float,
    private var rotationSpeed: Float,
    private var mouseSensitivity: Float
) : Behaviour() {

    private var isWireframe: Boolean = false

    private val camera: Camera
        get() = owner()?.getComponent<Camera>()!!

    private val movement = mutableMapOf<CameraMovement, Boolean>()
    private val rotation = mutableMapOf<CameraRotation, Boolean>()

    override fun create() {
        Events.subscribe<KeyPressedEvent, Any>(::onKeyPressed)
        Events.subscribe<KeyReleasedEvent, Any>(::onKeyReleased)
        Events.subscribe<MouseMovedEvent, Any>(::onMouseMoved)
        Events.subscribe<WindowResizedEvent, Any>(::onWindowResized)
    }

    override fun update(deltaTime: Float) {
        val keyboardInput = Resources.get<KeyboardInput>()!!

        movement[CameraMovement.FORWARD] = keyboardInput.isKeyHolding(GLFW.GLFW_KEY_W)
        movement[CameraMovement.BACKWARD] = keyboardInput.isKeyHolding(GLFW.GLFW_KEY_S)
        movement[CameraMovement.LEFT] = keyboardInput.isKeyHolding(GLFW.GLFW_KEY_A)
        movement[CameraMovement.RIGHT] = keyboardInput.isKeyHolding(GLFW.GLFW_KEY_D)

        movement.forEach { (direction, isPressed) ->
            if (isPressed) {
                processMovement(direction)
            }
        }

        rotation[CameraRotation.LEFT] = keyboardInput.isKeyHolding(GLFW.GLFW_KEY_LEFT)
        rotation[CameraRotation.RIGHT] = keyboardInput.isKeyHolding(GLFW.GLFW_KEY_RIGHT)

        rotation.forEach { (direction, isPressed) ->
            if (isPressed) {
                processRotation(direction)
            }
        }
    }

    override fun destroy() {
    }

    private fun onKeyPressed(event: KeyPressedEvent, sender: Any) {
        val key = event.key

        getMovementDirection(key)?.let { direction ->
            movement[direction] = true
        }

        getRotationDirection(key)?.let { direction ->
            rotation[direction] = true
        }

        when (key) {
            GLFW.GLFW_KEY_5 -> toggleWireframeMode()
        }
    }

    private fun onKeyReleased(event: KeyReleasedEvent, sender: Any) {
        val key = event.key

        getMovementDirection(key)?.let { direction ->
            movement[direction] = false
        }

        getRotationDirection(key)?.let { direction ->
            rotation[direction] = false
        }
    }

    private fun getMovementDirection(key: Int): CameraMovement? {
        return when (key) {
            GLFW.GLFW_KEY_W -> CameraMovement.FORWARD
            GLFW.GLFW_KEY_S -> CameraMovement.BACKWARD
            GLFW.GLFW_KEY_A -> CameraMovement.LEFT
            GLFW.GLFW_KEY_D -> CameraMovement.RIGHT
            else -> null
        }
    }

    private fun getRotationDirection(key: Int): CameraRotation? {
        return when (key) {
            GLFW.GLFW_KEY_LEFT -> CameraRotation.LEFT
            GLFW.GLFW_KEY_RIGHT -> CameraRotation.RIGHT
            GLFW.GLFW_KEY_UP -> CameraRotation.UP
            GLFW.GLFW_KEY_DOWN -> CameraRotation.DOWN
            else -> null
        }
    }

    private fun processMovement(direction: CameraMovement) {
        val velocity = moveSpeed //* deltaTime
        val forward = camera.forward().normalize()
        val up = camera.up().normalize()
        val right = camera.right()

        val offset = when (direction) {
            CameraMovement.FORWARD -> forward * velocity
            CameraMovement.BACKWARD -> -forward * velocity
            CameraMovement.LEFT -> -right * velocity
            CameraMovement.RIGHT -> right * velocity
            CameraMovement.UP -> -up * velocity
            CameraMovement.DOWN -> up * velocity
        }

        camera.move(offset)
    }

    private fun processRotation(direction: CameraRotation) {
        when (direction) {
            CameraRotation.LEFT -> camera.rotateAroundVerticalAxis(-rotationSpeed.toRadians())
            CameraRotation.RIGHT -> camera.rotateAroundVerticalAxis(rotationSpeed.toRadians())
            CameraRotation.UP -> camera.rotateAroundHorizontalAxis(rotationSpeed.toRadians())
            CameraRotation.DOWN -> camera.rotateAroundHorizontalAxis(-rotationSpeed.toRadians())
        }
    }

    private fun processMouseMovement(xOffset: Float, yOffset: Float) {
        val xOffsetModified = xOffset * mouseSensitivity * rotationSpeed
        val yOffsetModified = yOffset * mouseSensitivity * rotationSpeed

        camera.rotateAroundHorizontalAxis(yOffsetModified.toRadians())
        camera.rotateAroundVerticalAxis(xOffsetModified.toRadians())
    }

    private fun toggleWireframeMode() {
        if (isWireframe) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
        } else {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
        }

        isWireframe = !isWireframe
    }

    private fun onMouseMoved(event: MouseMovedEvent, sender: Any) {
        processMouseMovement(event.xOffset, event.yOffset)
    }

    private fun onWindowResized(event: WindowResizedEvent, sender: Any) {
        (camera as PerspectiveCamera).setProjParams(
            event.newWidth.toFloat(),
            event.newHeight.toFloat(),
            (camera as PerspectiveCamera).fovY,
            (camera as PerspectiveCamera).zNear,
            (camera as PerspectiveCamera).zFar
        )
    }
}