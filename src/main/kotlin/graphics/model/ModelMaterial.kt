package graphics.model

import core.math.Matrix4
import graphics.assets.surface.BaseMaterial
import graphics.assets.texture.Texture2d

class ModelMaterial: BaseMaterial<ModelMaterial, ModelShader>() {
    lateinit var worldViewProjection: Matrix4
    var isInstanced: Boolean = false
    var alphaThreshold: Float = 0.01f
    var ambientMap: Texture2d? = null
    var diffuseMap: Texture2d? = null
    var normalMap: Texture2d? = null
    var specularMap: Texture2d? = null

    fun updateByMtlData(mtlData: ModelMtlData?) {
        ambientMap = null
        diffuseMap = null
        normalMap = null
        specularMap = null

        mtlData?.textures?.forEach { (texType, texToken) ->
            when (texType) {
                ModelMaterialTextureType.AMBIENT_TEXTURE -> ambientMap = texToken.texture
                ModelMaterialTextureType.DIFFUSE_TEXTURE -> diffuseMap = texToken.texture
                ModelMaterialTextureType.SPECULAR_TEXTURE -> specularMap = texToken.texture
                ModelMaterialTextureType.NORMAL_TEXTURE -> normalMap = texToken.texture
            }
        }
    }
}