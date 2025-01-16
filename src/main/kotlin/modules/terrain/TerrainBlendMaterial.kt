package modules.terrain

import graphics.assets.surface.BaseMaterial
import graphics.assets.texture.Texture2d
import modules.terrain.heightmap.Heightmap

class TerrainBlendMaterial : BaseMaterial<TerrainBlendMaterial, TerrainBlendShader>() {
    lateinit var heightmap: Heightmap
    lateinit var normalmap: Texture2d
    lateinit var blendmap: Texture2d
    lateinit var elevationData: Array<ElevationData>
}