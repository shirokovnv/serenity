package graphics.model

import core.math.Vector2
import core.math.Vector3

data class ModelMeshData(
    val vertices: MutableList<Vertex> = mutableListOf(),
    val textures: MutableList<Vector2> = mutableListOf(),
    val normals: MutableList<Vector3> = mutableListOf(),
    val indices: MutableList<Int> = mutableListOf()
)

class ModelDataParser {
    fun parse(token: Token, modelMeshData: ModelMeshData) {
        when (token.type) {
            TokenType.VERTEX -> parseVertexToken(token.line, modelMeshData)
            TokenType.TEXTURE_COORDINATE -> parseTextureCoordinateToken(token.line, modelMeshData)
            TokenType.NORMAL -> parseNormalToken(token.line, modelMeshData)
            TokenType.FACE -> parseFaceToken(token.line, modelMeshData)
            TokenType.USEMTL -> {}
            TokenType.SMOOTHING_GROUP -> {}
            TokenType.COMMENT -> {}
            TokenType.OBJECT -> {}
            TokenType.UNKNOWN -> {}
        }
    }

    private fun parseVertexToken(line: String, modelMeshData: ModelMeshData) {
        val currentLine = line.split(" ")
        val vertex = Vector3(
            currentLine[1].toFloat(),
            currentLine[2].toFloat(),
            currentLine[3].toFloat()
        )
        val newVertex = Vertex(modelMeshData.vertices.size, vertex)
        modelMeshData.vertices.add(newVertex)
    }

    private fun parseTextureCoordinateToken(line: String, modelMeshData: ModelMeshData) {
        val currentLine = line.split(" ")
        val texture = Vector2(
            currentLine[1].toFloat(),
            currentLine[2].toFloat()
        )
        modelMeshData.textures.add(texture)
    }

    private fun parseNormalToken(line: String, modelMeshData: ModelMeshData) {
        val currentLine = line.split(" ")
        val normal = Vector3(
            currentLine[1].toFloat(),
            currentLine[2].toFloat(),
            currentLine[3].toFloat()
        )
        modelMeshData.normals.add(normal)
    }

    private fun parseFaceToken(line: String, modelMeshData: ModelMeshData) {
        val currentLine = line.split(" ")
        if (currentLine.size == 4) {
            val vertex1 = currentLine[1].split("/")
            val vertex2 = currentLine[2].split("/")
            val vertex3 = currentLine[3].split("/")
            processVertex(vertex1.toTypedArray(), modelMeshData.vertices, modelMeshData.indices)
            processVertex(vertex2.toTypedArray(), modelMeshData.vertices, modelMeshData.indices)
            processVertex(vertex3.toTypedArray(), modelMeshData.vertices, modelMeshData.indices)
        } else if (currentLine.size == 5) {
            val vertex1 = currentLine[1].split("/")
            val vertex2 = currentLine[2].split("/")
            val vertex3 = currentLine[3].split("/")
            val vertex4 = currentLine[4].split("/")

            // Triangle 1: v1, v2, v3
            processVertex(vertex1.toTypedArray(), modelMeshData.vertices, modelMeshData.indices)
            processVertex(vertex2.toTypedArray(), modelMeshData.vertices, modelMeshData.indices)
            processVertex(vertex3.toTypedArray(), modelMeshData.vertices, modelMeshData.indices)

            // Triangle 2: v1, v3, v4
            processVertex(vertex1.toTypedArray(), modelMeshData.vertices, modelMeshData.indices)
            processVertex(vertex3.toTypedArray(), modelMeshData.vertices, modelMeshData.indices)
            processVertex(vertex4.toTypedArray(), modelMeshData.vertices, modelMeshData.indices)
        }
    }

    private fun processVertex(vertexData: Array<String>, vertices: MutableList<Vertex>, indices: MutableList<Int>) {
        val index = vertexData[0].toInt() - 1
        val currentVertex = vertices[index]
        val textureIndex = vertexData[1].toInt() - 1
        val normalIndex = vertexData[2].toInt() - 1

        if (!currentVertex.isSet()) {
            currentVertex.textureIndex = textureIndex
            currentVertex.normalIndex = normalIndex
            indices.add(index)
        } else {
            dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices, vertices)
        }
    }

    private fun dealWithAlreadyProcessedVertex(
        previousVertex: Vertex,
        newTextureIndex: Int,
        newNormalIndex: Int,
        indices: MutableList<Int>,
        vertices: MutableList<Vertex>
    ) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.index)
        } else {
            val anotherVertex = previousVertex.duplicateVertex
            if (anotherVertex != null) {
                dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex, indices, vertices)
            } else {
                val duplicateVertex = Vertex(vertices.size, previousVertex.position)
                duplicateVertex.textureIndex = newTextureIndex
                duplicateVertex.normalIndex = newNormalIndex
                previousVertex.duplicateVertex = duplicateVertex
                vertices.add(duplicateVertex)
                indices.add(duplicateVertex.index)
            }
        }
    }
}