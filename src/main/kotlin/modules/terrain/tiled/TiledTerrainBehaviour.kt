package modules.terrain.tiled

import core.ecs.Behaviour
import core.math.Vector2
import graphics.assets.surface.bind
import graphics.rendering.Renderer
import graphics.rendering.passes.NormalPass
import graphics.rendering.passes.RenderPass

class TiledTerrainBehaviour(private val config: TiledTerrainConfig) : Behaviour(), Renderer {
    private lateinit var materialParams: TiledTerrainMaterialParams
    private lateinit var material: TiledTerrainMaterial
    private lateinit var shader: TiledTerrainShader

    private lateinit var buffer: TiledTerrainBuffer

    override fun create() {
        materialParams = TiledTerrainMaterialParams()
        material = TiledTerrainMaterial().setParams(materialParams)
        shader = TiledTerrainShader()
        shader bind material

        val vertices = buildVertices()

        // INSTANCING
        val numRows = config.gridSize
        val numCols = config.gridSize
        val offsets = Array(numRows * numCols) { i ->
            val row = i / numCols
            val col = i % numCols
            Vector2(row.toFloat(), col.toFloat())
        }

        buffer = TiledTerrainBuffer(vertices, offsets)
    }

    override fun update(deltaTime: Float) {
        // DO NOTHING
    }

    override fun destroy() {
        buffer.destroy()
    }

    override fun render(pass: RenderPass) {
        buffer.draw()
    }

    override fun supportsRenderPass(pass: RenderPass): Boolean {
        return pass == NormalPass
    }

    private fun buildVertices(): Array<Vector2> {
        return arrayOf(
            Vector2(0f, 0f),
            Vector2(0f, 1f),
            Vector2(1f, 0f),
            Vector2(1f, 1f)
        )
    }
}