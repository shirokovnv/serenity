package graphics.assets.surface

import core.math.Vector3

class DefaultMaterialParams(
    var ambientColor: Vector3 = Vector3(0.0f),
    var diffuseColor: Vector3 = Vector3(0.0f),
    var specularColor: Vector3 = Vector3(0.0f),
    var shininess: Float = 32.0f,
    var transparency: Float = 1.0f,
    var diffuseTextureId: Int = 0,
    var specularTextureId: Int = 0,
    var normalMapId: Int = 0
) : MaterialParams