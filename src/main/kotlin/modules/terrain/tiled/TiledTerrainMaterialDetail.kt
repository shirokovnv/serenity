package modules.terrain.tiled

import graphics.assets.texture.Texture2d

data class TiledTerrainMaterialDetail(
    val diffuseMap: Texture2d,
    val normalMap: Texture2d,
    val displacementMap: Texture2d,
    val verticalScale: Float,
    val horizontalScale: Float
) {
    init {
        diffuseMap.bind()
        diffuseMap.trilinearFilter()

        normalMap.bind()
        normalMap.trilinearFilter()

        displacementMap.bind()
        displacementMap.trilinearFilter()
    }
}