package core.scene.components.camera

import core.ecs.Component
import core.math.Vector3
import core.math.toRadians

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
    var moveSpeed: Float,
    var mouseSensitivity: Float
): Component() {

    private val camera: Camera
        get() = owner()?.getComponent<Camera>()!!

    fun processKeyboard(direction: CameraMovement, deltaTime: Float) {
        val velocity = moveSpeed * deltaTime
        val forward = camera.forward().normalize()
        val up = camera.up().normalize()
        val right = camera.right()

        val offset = Vector3(0f, 0f, 0f)

        when(direction) {
            CameraMovement.FORWARD -> offset.z += velocity
            CameraMovement.BACKWARD -> offset.z -= velocity
            CameraMovement.LEFT -> offset.x -= velocity
            CameraMovement.RIGHT -> offset.x += velocity
            CameraMovement.UP -> offset.y += velocity
            CameraMovement.DOWN -> offset.y -= velocity
        }

        camera.move((right + up + forward) * offset)
    }

    fun processKeyboardForRotation(rotation: CameraRotation, deltaTime: Float) {
        val velocity = moveSpeed * deltaTime * 5

        when(rotation) {
            CameraRotation.LEFT -> camera.rotate(Vector3(0f, (-velocity).toRadians(), 0f))
            CameraRotation.RIGHT -> camera.rotate(Vector3(0f, velocity.toRadians(), 0f))
            CameraRotation.UP -> camera.rotate(Vector3(velocity.toRadians(), 0f, 0f))
            CameraRotation.DOWN -> camera.rotate(Vector3((-velocity).toRadians(), 0f, 0f))
        }
    }

    fun processMouseMovement(xOffset: Float, yOffset: Float) {
//        val xOffsetModified = xOffset * mouseSensitivity;
//        val yOffsetModified = yOffset * mouseSensitivity;
//
//        camera.rotate(Vector3(-yOffsetModified.toRadians(), xOffsetModified.toRadians(), 0f))
    }
}