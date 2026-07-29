package modules.terrain.quadtree

import core.math.Vector3
import modules.terrain.BaseTerrainMaterial
import kotlin.properties.Delegates

class QuadTreeTerrainMaterial : BaseTerrainMaterial<QuadTreeTerrainMaterial, QuadTreeTerrainShader>() {
    lateinit var camPos: Vector3
    lateinit var sunColor: Vector3
    lateinit var sunVector: Vector3
    var sunIntensity by Delegates.notNull<Float>()
    var scaleY by Delegates.notNull<Float>()
    var tessFactor by Delegates.notNull<Int>()
}