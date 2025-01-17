package graphics.model

import core.math.Vector2
import core.math.Vector3
import java.io.BufferedReader
import java.io.StringReader

class ModelLoader {

    fun load(objFileContent: String, mtlFileContent: String? = null): MutableMap<String, ModelData> {

        val mtls = mutableMapOf<String, ModelMaterial>()
        if (mtlFileContent != null) {
            val mtlParser = ModelMtlParser()
            mtls.putAll(mtlParser.parseMTL(mtlFileContent))
        } else {
            mtls[ModelMaterial.DEFAULT_MATERIAL_NAME] = ModelMaterial()
        }

        val materialsTokens = mutableMapOf<String, MutableList<Token>>()
        var currentMaterial = ModelMaterial.DEFAULT_MATERIAL_NAME

        val modelTokenizer = ModelDataTokenizer()
        val modelParser = ModelDataParser()

        val allTokens = mutableListOf<Token>()

        var line: String?
        BufferedReader(StringReader(objFileContent)).use { reader ->
            line = reader.readLine()
            while (line != null) {
                val token = modelTokenizer.tokenize(line!!)
                if (token.type == TokenType.USEMTL) {
                    currentMaterial = token.line.substringAfter("usemtl ").trim()
                    if (!materialsTokens.containsKey(currentMaterial)) {
                        materialsTokens[currentMaterial] = mutableListOf()
                    }
                }
                allTokens.add(token)
                line = reader.readLine()
            }
        }

        // If no materials, just create default
        if (!materialsTokens.containsKey(currentMaterial)) {
            materialsTokens[currentMaterial] = mutableListOf()
        }

        val materialsMeshData = mutableMapOf<String, ModelMeshData>()
        materialsTokens.forEach { (materialName, tokens) ->
            tokens.addAll(allTokens)

            val modelMeshData = ModelMeshData()
            tokens.forEach { token ->
                modelParser.parse(token, modelMeshData)
            }

            materialsMeshData[materialName] = modelMeshData
        }

        val materialsModelData = mutableMapOf<String, ModelData>()
        materialsMeshData.forEach { (materialName, modelMeshData) ->

            removeUnusedVertices(modelMeshData.vertices)
            val verticesArray = FloatArray(modelMeshData.vertices.size * 3)
            val texturesArray = FloatArray(modelMeshData.vertices.size * 2)
            val normalsArray = FloatArray(modelMeshData.vertices.size * 3)
            val furthest = convertDataToArrays(
                modelMeshData.vertices, modelMeshData.textures, modelMeshData.normals, verticesArray,
                texturesArray, normalsArray
            )
            val indicesArray = convertIndicesListToArray(modelMeshData.indices)

            materialsModelData[materialName] =
                ModelData(
                    verticesArray,
                    texturesArray,
                    normalsArray,
                    indicesArray,
                    furthest,
                    mtls[materialName]
                )
        }

        return materialsModelData
    }

    private fun removeUnusedVertices(vertices: MutableList<Vertex>) {
        for (vertex in vertices) {
            if (!vertex.isSet()) {
                vertex.textureIndex = 0
                vertex.normalIndex = 0
            }
        }
    }

    private fun convertDataToArrays(
        vertices: List<Vertex>,
        textures: List<Vector2>,
        normals: List<Vector3>,
        verticesArray: FloatArray,
        texturesArray: FloatArray,
        normalsArray: FloatArray
    ): Float {
        var furthestPoint = 0f
        for (i in vertices.indices) {
            val currentVertex = vertices[i]
            furthestPoint = maxOf(furthestPoint, currentVertex.length)
            val position = currentVertex.position
            val textureCoord = textures[currentVertex.textureIndex]
            val normalVector = normals[currentVertex.normalIndex]

            verticesArray[i * 3] = position.x
            verticesArray[i * 3 + 1] = position.y
            verticesArray[i * 3 + 2] = position.z
            texturesArray[i * 2] = textureCoord.x
            texturesArray[i * 2 + 1] = 1 - textureCoord.y
            normalsArray[i * 3] = normalVector.x
            normalsArray[i * 3 + 1] = normalVector.y
            normalsArray[i * 3 + 2] = normalVector.z
        }
        return furthestPoint
    }

    private fun convertIndicesListToArray(indices: List<Int>): IntArray = indices.toIntArray()
}