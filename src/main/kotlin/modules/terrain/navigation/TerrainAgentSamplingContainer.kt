package modules.terrain.navigation

import core.math.Vector2
import modules.terrain.sampling.SamplingContainerInterface

class TerrainAgentSamplingContainer(
    override var points: List<Vector2>,
    override var innerRadius: Float,
    override var outerRadius: Float
) : SamplingContainerInterface {

    fun reducePointsByObstacles(obstaclesContainer: SamplingContainerInterface) {
        val newPoints = mutableListOf<Vector2>()
        for (agentPosition in points) {

            var isOverlapped = false
            for (obstaclePosition in obstaclesContainer.points) {
                if ((obstaclePosition - agentPosition).lengthSquared() <= (innerRadius + obstaclesContainer.innerRadius) * (innerRadius + obstaclesContainer.innerRadius)) {
                    isOverlapped = true
                    continue
                }
            }

            if (!isOverlapped) {
                newPoints.add(agentPosition)
            }
        }

        points = newPoints.toList()
    }
}