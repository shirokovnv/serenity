package modules.terrain.objects

import core.math.Quaternion
import graphics.rendering.passes.ReflectionPass
import graphics.rendering.passes.RefractionPass
import graphics.rendering.passes.RenderPass
import modules.water.plane.WaterPlaneConstants

object ObjectProviders {
    val clipPlanes: Map<RenderPass, Quaternion>
        get() {
            return mapOf(
                RefractionPass to Quaternion(0f, -1f, 0f, WaterPlaneConstants.DEFAULT_WORLD_HEIGHT),
                ReflectionPass to Quaternion(0f, 1f, 0f, -WaterPlaneConstants.DEFAULT_WORLD_HEIGHT + 1.0f)
            )
        }
}