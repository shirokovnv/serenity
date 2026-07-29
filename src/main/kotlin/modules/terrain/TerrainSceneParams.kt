package modules.terrain

import core.math.Rect3d
import core.math.Vector3
import core.scene.camera.CameraSettings

data class TerrainSceneParams(
    val worldBounds: Rect3d = Rect3d(
        Vector3(-3000f),
        Vector3(3000f)
    ),
    val worldScale: Vector3 = Vector3(1600.0f, 360.0f, 1600.0f),
    val worldOffset: Vector3 = Vector3(0f),
    val cameraSettings: CameraSettings
)