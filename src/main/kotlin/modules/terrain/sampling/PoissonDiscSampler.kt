package modules.terrain.sampling

import core.math.Vector2
import kotlin.math.*
import kotlin.random.Random

class PoissonDiscSampler {
    fun generatePoints(
        params: PoissonDiscSamplerParams,
        validator: SamplingValidatorInterface
    ): List<Vector2> {
        val cellSize = params.radius / sqrt(2f)

        // TODO: sampleRegionSize can be determined from the heightmap
        val gridWidth = ceil(params.sampleRegionSize.x / cellSize).toInt()
        val gridHeight = ceil(params.sampleRegionSize.y / cellSize).toInt()
        val grid = Array(gridWidth) { IntArray(gridHeight) }
        val points = mutableListOf<Vector2>()
        val spawnPoints = mutableListOf<Vector2>()

        spawnPoints.add(params.sampleRegionSize / 2f)

        while (spawnPoints.isNotEmpty()) {
            val spawnIndex = Random.nextInt(spawnPoints.size)
            val spawnCentre = spawnPoints[spawnIndex]
            var candidateAccepted = false

            for (i in 0..<params.numSamplesBeforeRejection) {
                val angle = Random.nextFloat() * PI * 2f
                val dir = Vector2(sin(angle).toFloat(), cos(angle).toFloat())
                val candidate = spawnCentre + dir * Random.nextFloat() * (params.radius + params.radius)

                if (isValid(candidate, params, validator, cellSize, points, grid)) {
                    points.add(candidate)
                    spawnPoints.add(candidate)
                    grid[(candidate.x / cellSize).toInt()][(candidate.y / cellSize).toInt()] = points.size
                    candidateAccepted = true
                    break
                }
            }

            if (!candidateAccepted) {
                spawnPoints.removeAt(spawnIndex)
            }
        }

        return points
    }

    private fun isValid(
        candidate: Vector2,
        params: PoissonDiscSamplerParams,
        validator: SamplingValidatorInterface,
        cellSize: Float,
        points: List<Vector2>,
        grid: Array<IntArray>
    ): Boolean {
        if (candidate.x >= 0
            && candidate.x < params.sampleRegionSize.x
            && candidate.y >= 0
            && candidate.y < params.sampleRegionSize.y
        ) {
            if (!validator.validate(candidate)) {
                return false
            }

            val cellX = (candidate.x / cellSize).toInt()
            val cellY = (candidate.y / cellSize).toInt()
            val searchStartX = max(0, cellX - 2)
            val searchEndX = min(cellX + 2, grid.size - 1)
            val searchStartY = max(0, cellY - 2)
            val searchEndY = min(cellY + 2, grid[0].size - 1)

            for (x in searchStartX..searchEndX) {
                for (y in searchStartY..searchEndY) {
                    val pointIndex = grid[x][y] - 1
                    if (pointIndex != -1) {
                        val sqrDst = (candidate - points[pointIndex]).lengthSquared()
                        if (sqrDst < params.radius * params.radius) {
                            return false
                        }
                    }
                }
            }
            return true
        }
        return false
    }
}