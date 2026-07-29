package modules.terrain.tiled

import modules.terrain.BaseTerrainMaterial
import kotlin.properties.Delegates

class TiledTerrainMaterial : BaseTerrainMaterial<TiledTerrainMaterial, TiledTerrainShader>() {
    companion object {
        const val DEFAULT_MIN_DISTANCE = 1.0f
        const val DEFAULT_MAX_DISTANCE = 3000.0f
        const val DEFAULT_TBN_RANGE = 200.0f
        const val DEFAULT_MIN_LOD = 1.0f
        const val DEFAULT_MAX_LOD = 16.0f
    }

    var gridScale by Delegates.notNull<Float>()

    var minDistance by Delegates.notNull<Float>()
    var maxDistance by Delegates.notNull<Float>()
    var minLOD by Delegates.notNull<Float>()
    var maxLOD by Delegates.notNull<Float>()
    var scaleY by Delegates.notNull<Float>()
    var tbnRange: Float = 200.0f
    var tbnThreshold: Float = 50.0f
    var renderInBlack: Boolean = false
}