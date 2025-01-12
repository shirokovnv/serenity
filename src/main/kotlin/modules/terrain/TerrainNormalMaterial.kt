package modules.terrain

import graphics.assets.surface.BaseMaterial
import graphics.assets.texture.Texture2d
import kotlin.properties.Delegates

class TerrainNormalMaterial : BaseMaterial<TerrainNormalMaterial, TerrainNormalShader>() {
    lateinit var heightmap: Heightmap
    lateinit var normalmap: Texture2d
    var normalStrength by Delegates.notNull<Float>()
}