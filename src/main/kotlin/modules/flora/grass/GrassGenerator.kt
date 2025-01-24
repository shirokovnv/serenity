package modules.flora.grass

import core.math.Rect3d
import core.math.Vector2
import core.math.Vector3
import core.scene.BoxAABB
import core.scene.Transform
import graphics.assets.texture.TextureChannel
import graphics.model.Model
import modules.terrain.heightmap.Blendmap
import modules.terrain.heightmap.Heightmap
import kotlin.random.Random

class GrassGenerator {
    fun generateInstances(grassModel: Model, spacing: Float) {
        val grassStart = Vector2(0f, 0f)
        val grassStop = Vector2(1f, 1f)
        val rand = Random(System.currentTimeMillis())
        val distribution: (Random) -> Float = { rng -> (rng.nextFloat() * spacing - spacing / 2f) }

        var y = grassStart.y
        while (y < grassStop.y) {
            var x = grassStart.x
            while (x < grassStop.x) {
                val transform = Transform()
                transform.setTranslation(Vector3(x + distribution(rand), 0f, y + distribution(rand)))
                grassModel.addInstance(transform.matrix())
                x += spacing
            }
            y += spacing
        }
    }

    fun generatePatches(
        heightmap: Heightmap,
        blendmap: Blendmap,
        params: GrassPatchParams,
        channel: TextureChannel
    ): List<GrassPatch> {
        val patches = mutableListOf<GrassPatch>()

        val width = heightmap.texture().getWidth()
        val height = heightmap.texture().getHeight()

        if (width != blendmap.texture().getWidth() || height != blendmap.texture().getHeight()) {
            throw IllegalArgumentException("Heightmap and Blendmap not correlate.")
        }

        val worldScale = heightmap.worldScale()
        val worldOffset = heightmap.worldOffset()

        val scaleX = worldScale.x / width
        val scaleY = worldScale.y
        val scaleZ = worldScale.z / height

        var yIndex = 0
        while (yIndex < height) {
            var xIndex = 0
            while (xIndex < width) {
                var patchAccepted = true

                var minHeight = Float.MAX_VALUE
                var maxHeight = Float.MIN_VALUE

                for (x in xIndex..<xIndex + params.cellSize) {
                    for (y in yIndex..<yIndex + params.cellSize) {
                        if (!isValidGrassPoint(blendmap, channel, x, y)) {
                            patchAccepted = false
                            break
                        }

                        val currentHeight = heightmap.getHeightAt(x, y)

                        if (minHeight > currentHeight) {
                            minHeight = currentHeight
                        }

                        if (maxHeight < currentHeight) {
                            maxHeight = currentHeight
                        }
                    }
                }

                if (patchAccepted) {
                    val patchCorner0 = Vector2(xIndex.toFloat(), yIndex.toFloat())
                    val patchCorner1 = Vector2(
                        xIndex.toFloat() + params.cellSize - 1,
                            yIndex.toFloat() + params.cellSize - 1
                        )

                    val grassPatch = GrassPatch()
                    val transform = grassPatch.getComponent<Transform>()!!
                    transform.setScale(
                        Vector3(
                            scaleX * params.cellSize,
                            params.verticalScale * params.cellSize,
                            scaleZ * params.cellSize
                        )
                    )
                    transform.setTranslation(
                        Vector3(
                            patchCorner0.x * scaleX + worldOffset.x,
                            minHeight * scaleY + worldOffset.y,
                            patchCorner0.y * scaleZ + worldOffset.z
                        )
                    )

                    val p0 = Vector3(patchCorner0.x * scaleX, minHeight * scaleY, patchCorner0.y * scaleZ)
                    val p1 = Vector3(patchCorner1.x * scaleX, maxHeight * scaleY, patchCorner1.y * scaleZ)
                    val patchBounds = Rect3d(p0, p1)
                    grassPatch.getComponent<BoxAABB>()!!.setShape(patchBounds)

                    patches.add(grassPatch)
                }

                xIndex += params.cellSize
            }
            yIndex += params.cellSize
        }

        return patches
    }

    private fun isValidGrassPoint(blendmap: Blendmap, channel: TextureChannel, x: Int, y: Int): Boolean {
        val blendValue = blendmap.readRGBA(x, y)
        return when(channel) {
            TextureChannel.R -> blendValue.x != 0.0f && blendValue.x > blendValue.y && blendValue.x > blendValue.z
            TextureChannel.G -> blendValue.y != 0.0f && blendValue.y > blendValue.x && blendValue.y > blendValue.z
            TextureChannel.B -> blendValue.z != 0.0f && blendValue.z > blendValue.x && blendValue.z > blendValue.y
            TextureChannel.A -> throw IllegalArgumentException("Cannot use alpha channel.")
        }
    }
}