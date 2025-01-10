package modules.terrain.tiled

import graphics.assets.surface.BaseShader

class TiledTerrainShader: BaseShader<TiledTerrainShader, TiledTerrainMaterial, TiledTerrainMaterialParams>() {
    private var material: TiledTerrainMaterial? = null

    override fun setMaterial(material: TiledTerrainMaterial?) {
        this.material = material
    }

    override fun getMaterial(): TiledTerrainMaterial? {
        return material
    }
}