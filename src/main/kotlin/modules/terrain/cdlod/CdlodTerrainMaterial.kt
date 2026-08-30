package modules.terrain.cdlod

import core.math.Vector3
import modules.terrain.BaseTerrainMaterial
import kotlin.properties.Delegates

class CdlodTerrainMaterial : BaseTerrainMaterial<CdlodTerrainMaterial, CdlodTerrainShader>() {
    lateinit var camPos: Vector3
    lateinit var lodRanges: FloatArray
    var resolution by Delegates.notNull<Float>()

    lateinit var sunColor: Vector3
    lateinit var sunVector: Vector3
    var sunIntensity by Delegates.notNull<Float>()
}