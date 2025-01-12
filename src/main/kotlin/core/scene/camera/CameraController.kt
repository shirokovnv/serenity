package core.scene.camera

import core.ecs.Behaviour
import core.math.Vector3
import core.math.extensions.toRadians
import core.scene.Object
import org.lwjgl.glfw.GLFW
import platform.services.input.KeyboardInput
import platform.services.input.KeyboardInputListener
import platform.services.input.MouseInput
import platform.services.input.MouseInputListener

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
    private var mouseSensitivity: Float
): Behaviour(), KeyboardInputListener, MouseInputListener {

    private val camera: Camera
        get() = owner()?.getComponent<Camera>()!!

    private val movement = mutableMapOf<CameraMovement, Boolean>()

    override fun create() {
        Object.services.getService<KeyboardInput>()!!.addListener(this)
        Object.services.getService<MouseInput>()!!.addListener(this)
    }

    override fun update(deltaTime: Float) {
        val keyboardInput = Object.services.getService<KeyboardInput>()!!

        movement[CameraMovement.FORWARD] = keyboardInput.isKeyHolding(GLFW.GLFW_KEY_W)
        movement[CameraMovement.BACKWARD] = keyboardInput.isKeyHolding(GLFW.GLFW_KEY_S)
        movement[CameraMovement.LEFT] = keyboardInput.isKeyHolding(GLFW.GLFW_KEY_A)
        movement[CameraMovement.RIGHT] = keyboardInput.isKeyHolding(GLFW.GLFW_KEY_D)

        movement.forEach { (direction, isPressed) ->
            if(isPressed){
                processMovement(direction, deltaTime)
            }
        }
    }

    override fun destroy() {

    }

    override fun onKeyPressed(key: Int) {
        getDirection(key)?.let { direction ->
            movement[direction] = true
        }
    }

    override fun onKeyReleased(key: Int) {
        getDirection(key)?.let { direction ->
            movement[direction] = false
        }
    }

    private fun getDirection(key: Int): CameraMovement? {
        return when (key) {
            GLFW.GLFW_KEY_W -> CameraMovement.FORWARD
            GLFW.GLFW_KEY_S -> CameraMovement.BACKWARD
            GLFW.GLFW_KEY_A -> CameraMovement.LEFT
            GLFW.GLFW_KEY_D -> CameraMovement.RIGHT
            else -> null
        }
    }

    private fun processMovement(direction: CameraMovement, deltaTime: Float = 1f) {
        val velocity = moveSpeed //* deltaTime
        val forward = camera.forward().normalize()
        val up = camera.up().normalize()
        val right = camera.right()

        val offset = when (direction) {
            CameraMovement.FORWARD -> -forward * velocity //Vector3(0f, 0f, -velocity)
            CameraMovement.BACKWARD -> forward * velocity //Vector3(0f, 0f, velocity)
            CameraMovement.LEFT -> -right * velocity //  Vector3(-velocity, 0f, 0f)
            CameraMovement.RIGHT -> right * velocity // Vector3(velocity, 0f, 0f)
            CameraMovement.UP -> up * velocity
            CameraMovement.DOWN -> -up * velocity
        }

        camera.move(offset)
    }

    private fun processMouseMovement(xOffset: Float, yOffset: Float) {
        val xOffsetModified = xOffset * mouseSensitivity
        val yOffsetModified = yOffset * mouseSensitivity

        camera.rotate(Vector3(yOffsetModified.toRadians(), -xOffsetModified.toRadians(), 0f))
    }

    override fun onMouseMoved(xOffset: Float, yOffset: Float) {
        processMouseMovement(xOffset, yOffset)
    }

    override fun onMouseScrolled(yOffset: Float) {

    }

    override fun onMouseButtonPressed(button: Int) {

    }

    override fun onMouseButtonReleased(button: Int) {

    }
}