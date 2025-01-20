package core.scene.camera

import core.ecs.Behaviour
import core.math.Vector3
import core.math.extensions.toRadians
import core.scene.Object
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
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

    private var isWireframe: Boolean = false

    private val camera: Camera
        get() = owner()?.getComponent<Camera>()!!

    private val movement = mutableMapOf<CameraMovement, Boolean>()
    private val rotation = mutableMapOf<CameraRotation, Boolean>()

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
                processMovement(direction)
            }
        }

        rotation[CameraRotation.LEFT] = keyboardInput.isKeyHolding(GLFW.GLFW_KEY_LEFT)
        rotation[CameraRotation.RIGHT] = keyboardInput.isKeyHolding(GLFW.GLFW_KEY_RIGHT)

        rotation.forEach{ (direction, isPressed) ->
            if (isPressed) {
                processRotation(direction)
            }
        }
    }

    override fun destroy() {

    }

    override fun onKeyPressed(key: Int) {
        getMovementDirection(key)?.let { direction ->
            movement[direction] = true
        }

        getRotationDirection(key)?.let { direction ->
            rotation[direction] = true
        }

        when(key) {
            GLFW.GLFW_KEY_5 -> toggleWireframeMode()
        }
    }

    override fun onKeyReleased(key: Int) {
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
        return when(key) {
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
            CameraMovement.FORWARD -> -forward * velocity
            CameraMovement.BACKWARD -> forward * velocity
            CameraMovement.LEFT -> -right * velocity
            CameraMovement.RIGHT -> right * velocity
            CameraMovement.UP -> up * velocity
            CameraMovement.DOWN -> -up * velocity
        }

        camera.move(offset)
    }

    private fun processRotation(direction: CameraRotation) {
        val rotationSpeed = 0.2f

        when(direction) {
            CameraRotation.LEFT -> camera.rotate(Vector3(0f, rotationSpeed.toRadians(), 0f))
            CameraRotation.RIGHT -> camera.rotate(Vector3(0f, -rotationSpeed.toRadians(), 0f))
            CameraRotation.UP -> camera.rotate(Vector3(rotationSpeed.toRadians(), 0f, 0f))
            CameraRotation.DOWN -> camera.rotate(Vector3(-rotationSpeed.toRadians(), 0f, 0f))
        }
    }

    private fun processMouseMovement(xOffset: Float, yOffset: Float) {
        val xOffsetModified = xOffset * mouseSensitivity
        val yOffsetModified = yOffset * mouseSensitivity

        camera.rotate(Vector3(yOffsetModified.toRadians(), -xOffsetModified.toRadians(), 0f))
    }

    private fun toggleWireframeMode() {
        if (isWireframe) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
        } else {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
        }

        isWireframe = !isWireframe
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