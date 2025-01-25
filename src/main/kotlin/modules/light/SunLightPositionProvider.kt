package modules.light

import core.management.Resources
import core.math.Quaternion
import core.math.Vector2
import core.scene.camera.Camera

const val SUN_DISTANCE = 100000f

fun defaultSunScreenPositionProvider(): Vector2? {
    val sunLightManager = Resources.get<SunLightManager>() ?: return null
    val camera = Resources.get<Camera>() ?: return null

    val sunWorldPosition = sunLightManager.sunVector() * SUN_DISTANCE

    val sunVector = Quaternion(sunWorldPosition, 1f)

    if (sunVector.y < 0) {
        return null
    }

    val sunVectorScreenPosition = camera.viewProjection * sunVector

    if (sunVectorScreenPosition.w <= 0) {
        return null
    }

    return Vector2(
        (sunVectorScreenPosition.x / sunVectorScreenPosition.w + 1) / 2,
        1 - (sunVectorScreenPosition.y / sunVectorScreenPosition.w + 1) / 2
    )
}