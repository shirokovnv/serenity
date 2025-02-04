package core.scene.raytracing

import core.math.Quaternion
import core.math.Vector2
import core.math.Vector3
import core.scene.camera.PerspectiveCamera

class RayTracer(
    private val camera: PerspectiveCamera,
) {

    fun calculateRay(screenX: Float, screenY: Float): Vector3 {
        val ndc = getNdc(screenX, screenY)
        val clipCoordinates = Quaternion(ndc.x, ndc.y, -1.0f, 1.0f)
        val eyeCoordinates = getEyeCoordinates(clipCoordinates)

        return getWorldCoordinates(eyeCoordinates)
    }

    fun traceToDistance(ray: Vector3, distance: Float): Vector3 {
        return getPointOnRay(camera.position(), ray, distance)
    }

    private fun getNdc(screenX: Float, screenY: Float): Vector2 {
        val x = (2.0f * screenX) / camera.width - 1f
        val y = (2.0f * screenY) / camera.height - 1f

        return Vector2(x, y)
    }

    private fun getEyeCoordinates(clipCoordinates: Quaternion): Quaternion {
        val eyeCoordinates = camera.projection.invert() * clipCoordinates
        return Quaternion(eyeCoordinates.x, eyeCoordinates.y, -1f, 0f)
    }

    private fun getWorldCoordinates(eyeCoordinates: Quaternion): Vector3 {
        return (camera.view.invert() * eyeCoordinates).xyz().normalize()
    }
}