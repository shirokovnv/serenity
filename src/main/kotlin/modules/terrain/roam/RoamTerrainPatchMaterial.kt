package modules.terrain.roam

import core.math.Vector2
import core.math.Vector3
import modules.terrain.BaseTerrainMaterial
import kotlin.properties.Delegates

class RoamTerrainPatchMaterial : BaseTerrainMaterial<RoamTerrainPatchMaterial, RoamTerrainPatchShader>() {
    val textureSize: Vector2
        get() = Vector2(
            heightmap.texture().getWidth().toFloat(),
            heightmap.texture().getHeight().toFloat()
        )

    lateinit var cameraPosition: Vector3
    lateinit var sunVector: Vector3
    lateinit var sunColor: Vector3
    var sunIntensity by Delegates.notNull<Float>()
    var scaleY: Float = 0.0f
}