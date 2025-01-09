package graphics.assets.surface

import core.math.Vector3

data class DefaultMaterialParams(
    val ambientColor: Vector3 = Vector3(0.0f),
    val diffuseColor: Vector3 = Vector3(0.0f),
    val specularColor: Vector3 = Vector3(0.0f),
    val shininess: Float = 32.0f,
    val transparency: Float = 1.0f,
    val diffuseTextureId: Int = 0,
    val specularTextureId: Int = 0,
    val normalMapId: Int = 0
) : MaterialParams