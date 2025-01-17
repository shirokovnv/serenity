package graphics.model

import core.math.Vector3

class ModelMtlParser {
    fun parseMTL(mtlContent: String): MutableMap<String, ModelMaterial> {
        val materials = mutableMapOf<String, ModelMaterial>()
        var currentMaterial = ModelMaterial()

        mtlContent.lineSequence()
            .filter { it.isNotBlank() }
            .map { it.trim() }
            .forEach { line ->
                val parts = line.split(" ", limit = 2)
                val keyword = parts[0].lowercase()
                val value = if (parts.size > 1) parts[1] else ""

                when (keyword) {
                    "newmtl" -> {
                        currentMaterial = ModelMaterial()
                        currentMaterial.name = value
                        materials[value] = currentMaterial
                    }

                    "ka" -> currentMaterial.ambientColor = parseVector3String(value)
                    "kd" -> currentMaterial.diffuseColor = parseVector3String(value)
                    "ks" -> currentMaterial.specularColor = parseVector3String(value)
                    "ns" -> currentMaterial.shininess = value.toFloatOrNull() ?: 0f
                    "map_kd" -> currentMaterial.textures[ModelMaterialTextureType.DIFFUSE_TEXTURE] =
                        ModelMaterialTextureToken(value)

                    "map_ka" -> currentMaterial.textures[ModelMaterialTextureType.AMBIENT_TEXTURE] =
                        ModelMaterialTextureToken(value)

                    "map_ks" -> currentMaterial.textures[ModelMaterialTextureType.SPECULAR_TEXTURE] =
                        ModelMaterialTextureToken(value)

                    "map_bump" -> currentMaterial.textures[ModelMaterialTextureType.NORMAL_TEXTURE] =
                        ModelMaterialTextureToken(value)

                    else -> {}
                }
            }

        return materials
    }

    private fun parseVector3String(value: String): Vector3 {
        val parts = value.split(" ")
        return Vector3(parts[0].toFloat(), parts[1].toFloat(), parts[2].toFloat())
    }
}