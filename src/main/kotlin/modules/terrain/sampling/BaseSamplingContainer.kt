package modules.terrain.sampling

import core.math.Vector2

abstract class BaseSamplingContainer: SamplingContainerInterface {
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