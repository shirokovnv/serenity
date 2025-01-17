package graphics.model

import core.math.Vector3
import graphics.assets.texture.Texture2d

enum class ModelMaterialTextureType {
    AMBIENT_TEXTURE,
    DIFFUSE_TEXTURE,
    SPECULAR_TEXTURE,
    NORMAL_TEXTURE
}

class ModelMaterialTextureToken(var name: String? = null, var texture: Texture2d? = null)

class ModelMtlData {

    companion object {
        const val DEFAULT_MATERIAL_NAME = "default"
    }

    var name: String = DEFAULT_MATERIAL_NAME
    var ambientColor: Vector3 = Vector3(0.2f, 0.2f, 0.2f)
    var diffuseColor: Vector3 = Vector3(0.8f, 0.8f, 0.8f)
    var specularColor: Vector3 = Vector3(0.0f, 0.0f, 0.0f)
    var shininess: Float = 0.0f

    var textures = HashMap<ModelMaterialTextureType, ModelMaterialTextureToken>()

    override fun toString(): String {
        return "ModelMaterial(name=$name, " +
                "ambientColor=$ambientColor, " +
                "diffuseColor=$diffuseColor, " +
                "specularColor=$specularColor, " +
                "shininess=$shininess, " +
                "textures=${textures.values}"
    }
}