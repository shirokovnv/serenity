package modules.terrain.tiled

import core.math.Matrix4
import graphics.assets.surface.MaterialParams
import modules.terrain.Heightmap
import kotlin.properties.Delegates

class TiledTerrainMaterialParams: MaterialParams {
    lateinit var world: Matrix4
    lateinit var view: Matrix4
    lateinit var viewProjection: Matrix4
    var gridScale by Delegates.notNull<Float>()
    lateinit var heightmap: Heightmap
    var minDistance by Delegates.notNull<Float>()
    var maxDistance by Delegates.notNull<Float>()
    var minLOD by Delegates.notNull<Float>()
    var maxLOD by Delegates.notNull<Float>()
    var scaleY by Delegates.notNull<Float>()
}