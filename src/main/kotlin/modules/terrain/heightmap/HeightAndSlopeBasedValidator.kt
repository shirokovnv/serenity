package modules.terrain.heightmap

import core.math.Vector2
import core.math.Vector3
import modules.terrain.sampling.SamplingValidatorInterface

class HeightAndSlopeBasedValidator(
    private val heightmap: Heightmap,
    private val minHeight: Float = 0.0f,
    private val maxHeight: Float = 1.0f,
    private val maxSlope: Float = 0.7f
) : SamplingValidatorInterface {
    companion object {
        private val upVector = Vector3(0f, 1f, 0f)
    }

    override fun validate(point: Vector2): Boolean {
        val worldX = point.x - heightmap.worldOffset().x
        val worldY = point.y - heightmap.worldOffset().z

        val height = heightmap.getInterpolatedHeight(worldX, worldY)

        if (height < minHeight || height > maxHeight) {
            return false
        }

        val normal = heightmap.getInterpolatedNormal(worldX, worldY)
        val slope = normal.dot(upVector)

        if(slope < maxSlope){
            return false
        }

        return true
    }
}