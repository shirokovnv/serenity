package modules.terrain.tiled

import graphics.assets.surface.BaseMaterial

class TiledTerrainMaterial : BaseMaterial<TiledTerrainMaterial, TiledTerrainMaterialParams, TiledTerrainShader>() {
    private var shader: TiledTerrainShader? = null
    private lateinit var materialParams: TiledTerrainMaterialParams

    override fun setShader(shader: TiledTerrainShader?) {
        this.shader = shader
    }

    override fun getShader(): TiledTerrainShader? {
        return shader
    }

    override fun getParams(): TiledTerrainMaterialParams {
        return materialParams
    }

    override fun setParams(params: TiledTerrainMaterialParams): TiledTerrainMaterial {
        this.materialParams = params
        return this
    }
}