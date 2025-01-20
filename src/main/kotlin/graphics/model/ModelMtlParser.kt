package graphics.model

import core.math.Vector3

class ModelMtlParser {
    fun parseMTL(mtlContent: String): MutableMap<String, ModelMtlData> {
        val materials = mutableMapOf<String, ModelMtlData>()
        var currentMaterial = ModelMtlData()

        mtlContent.lineSequence()
            .filter { it.isNotBlank() }
            .map { it.trim() }
            .forEach { line ->
                val parts = line.split(" ", limit = 2)
                val keyword = parts[0].lowercase()
                val value = if (parts.size > 1) parts[1] else ""

                when (keyword) {
                    "newmtl" -> {
                        currentMaterial = ModelMtlData()
                        currentMaterial.name = value
                        materials[value] = currentMaterial
                    }

                    "ka" -> currentMaterial.ambientColor = parseVector3String(value)
                    "kd" -> currentMaterial.diffuseColor = parseVector3String(value)
                    "ks" -> currentMaterial.specularColor = parseVector3String(value)
                    "ns" -> currentMaterial.shininess = value.toFloatOrNull() ?: 0f
                    "map_kd" -> currentMaterial.textures[TextureType.DIFFUSE_TEXTURE] =
                        TextureToken(value)

                    "map_ka" -> currentMaterial.textures[TextureType.AMBIENT_TEXTURE] =
                        TextureToken(value)

                    "map_ks" -> currentMaterial.textures[TextureType.SPECULAR_TEXTURE] =
                        TextureToken(value)

                    "map_bump" -> currentMaterial.textures[TextureType.NORMAL_TEXTURE] =
                        TextureToken(value)

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