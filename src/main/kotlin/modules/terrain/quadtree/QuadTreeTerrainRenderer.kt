package modules.terrain.quadtree

import core.ecs.BaseComponent
import core.math.Quaternion
import core.math.Vector2
import core.math.Vector3
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass

class QuadTreeTerrainRenderer(
    private val terrainNode: QuadTreeTerrainNode,
    private val buffer: QuadTreeBuffer,
    private val shader: QuadTreeTerrainShader,
    private val material: QuadTreeTerrainMaterial
): BaseComponent(), Renderer {

    override fun render(pass: RenderPass) {

        val bufferData = terrainNode.recursiveCollectInstanceData()
        val numInstances = bufferData.size

        val locations = Array(numInstances) { Vector2(0f, 0f) }
        val scales   = Array(numInstances) { Vector3(0f, 0f, 0f) }
        val lodVectors = Array(numInstances) { Quaternion(0f, 0f, 0f, 0f) }
        val lowPoints = Array(numInstances) { Vector3(0f, 0f, 0f) }
        val highPoints = Array(numInstances) { Vector3(0f, 0f, 0f) }

        bufferData.forEachIndexed { index, item ->
            locations[index] = item.topLeft
            scales[index] = item.scale
            lodVectors[index] = item.lod
            lowPoints[index] = item.lowPoint
            highPoints[index] = item.highPoint
        }

        buffer.uploadData(
            locations, scales, lodVectors, lowPoints, highPoints
        )

        shader.bind()
        shader.updateUniforms()
        buffer.bind()
        buffer.primitiveType = QuadTreeBuffer.PrimitiveType.PATCH
        buffer.draw()
        buffer.unbind()
        shader.unbind()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }
}