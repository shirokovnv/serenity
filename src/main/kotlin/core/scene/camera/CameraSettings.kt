package core.scene.camera

import core.math.Vector3
import core.math.extensions.toRadians

data class CameraSettings(
    val width: Int,
    val height: Int,
    val fov: Float = 70.0f,
    val zNear: Float = 0.1f,
    val zFar: Float = 3000.0f,
    val moveSpeed: Float = 0.5f,
    val rotationSpeed: Float = 1.5f,
    val sensitivity: Float = 0.1f,
    val initialTranslation: Vector3 = Vector3(0f, 300f, 0f),
    val initialRotation: Vector3 = Vector3(0f, 90f.toRadians(), 0f)
) {
    init {
        require(width >= 0)
        require(height >= 0)
        require(fov in 30.0f..90.0f)
        require(zNear < zFar)
    }
}