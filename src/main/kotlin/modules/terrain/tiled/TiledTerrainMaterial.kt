package modules.terrain.tiled

import core.math.Matrix4
import graphics.assets.surface.BaseMaterial
import graphics.assets.texture.Texture2d
import modules.terrain.Heightmap
import kotlin.properties.Delegates

class TiledTerrainMaterial : BaseMaterial<TiledTerrainMaterial, TiledTerrainShader>() {
    lateinit var world: Matrix4
    lateinit var view: Matrix4
    lateinit var viewProjection: Matrix4
    var gridScale by Delegates.notNull<Float>()
    lateinit var heightmap: Heightmap
    lateinit var normalmap: Texture2d
    lateinit var blendmap: Texture2d
    lateinit var grassTexture: Texture2d
    lateinit var dirtTexture: Texture2d
    lateinit var rockTexture: Texture2d
    var minDistance by Delegates.notNull<Float>()
    var maxDistance by Delegates.notNull<Float>()
    var minLOD by Delegates.notNull<Float>()
    var maxLOD by Delegates.notNull<Float>()
    var scaleY by Delegates.notNull<Float>()
}