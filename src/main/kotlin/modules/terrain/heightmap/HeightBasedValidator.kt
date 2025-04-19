package modules.terrain.heightmap

import core.math.Vector2
import modules.terrain.sampling.SamplingValidatorInterface

class HeightBasedValidator(
    private val heightmap: Heightmap,
    private val minHeight: Float = 0.0f,
    private val maxHeight: Float = 1.0f
) : SamplingValidatorInterface {
    override fun validate(point: Vector2): Boolean {
        val worldX = point.x - heightmap.worldOffset().x
        val worldY = point.y - heightmap.worldOffset().z

        val height = heightmap.getInterpolatedHeight(worldX, worldY)

        return !(height < minHeight || height > maxHeight)
    }
}