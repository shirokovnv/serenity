package modules.terrain

import core.math.Matrix4
import core.math.Quaternion
import graphics.assets.surface.BaseMaterial
import graphics.assets.texture.Texture2d
import modules.terrain.heightmap.Heightmap

abstract class BaseTerrainMaterial<M: BaseTerrainMaterial<M, S>, S: BaseTerrainShader<S, M>> :
    BaseMaterial<M, S>() {

    lateinit var lightViewProjection: Matrix4
    lateinit var clipPlane: Quaternion

    lateinit var model: Matrix4
    lateinit var world: Matrix4
    lateinit var view: Matrix4
    lateinit var projection: Matrix4
    lateinit var viewProjection: Matrix4

    lateinit var heightmap: Heightmap
    lateinit var normalmap: Texture2d
    lateinit var blendmap: Texture2d
    lateinit var shadowmap: Texture2d

    var materialDetailMap = HashMap<TerrainTextureType, TerrainMaterialDetail>()
}