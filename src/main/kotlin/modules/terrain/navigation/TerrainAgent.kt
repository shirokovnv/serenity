package modules.terrain.navigation

import core.math.Matrix4
import core.math.Rect3d
import core.math.Vector3
import core.scene.navigation.NavGrid
import core.scene.navigation.agents.BaseNavMeshAgent
import core.scene.navigation.agents.NavMeshAgent
import core.scene.navigation.heuristics.DiagonalDistanceHeuristic
import core.scene.navigation.heuristics.PathHeuristicInterface
import core.scene.navigation.obstacles.NavMeshObstacle
import core.scene.navigation.steering.NeighboursProvider
import core.scene.navigation.steering.ObstaclesProvider
import core.scene.navigation.steering.SteeringAgent
import core.scene.volumes.BoxAABB

class TerrainAgent(
    initialWorldPosition: Vector3,
    private val navGrid: NavGrid
) : BaseNavMeshAgent(initialWorldPosition), SteeringAgent {
    override val pathHeuristic: PathHeuristicInterface
        get() = DiagonalDistanceHeuristic()

    override var radius: Float = 10.0f
    override var maxSlope: Float = 0.5f
    override var stepSize: Float = 5.0f

    init {
        recalculateBounds()
    }

    override fun recalculateBounds() {
        bounds().setShape(Rect3d(getAgentBounds().shape()))
    }

    override var isStatic: Boolean = false

    override var position: Vector3
        get() = transform().translation()
        set(value) {
            transform().setTranslation(value)
        }

    override var velocity: Vector3 = Vector3(0f)
    override var acceleration: Vector3 = Vector3(0f)
    override var maxSpeed: Float = 0.1f
    override var maxForce: Float = 0.1f

    override var orientation: Matrix4
        get() = TODO("Not yet implemented")
        set(value) {}

    override var perceptionDistance: Float = 25.0f
    override var target: Vector3 = Vector3(0f)

    override var avoidanceDistance: Float = 15.0f
    override var avoidanceRadius: Float = 15.0f

    override val neighbours: NeighboursProvider
        get() {
            val searchVolume = BoxAABB(
                Rect3d(
                    Vector3(position - perceptionDistance),
                    Vector3(position + perceptionDistance)
                )
            )

            return {
                navGrid
                    .buildSearchResults(searchVolume)
                    .filterIsInstance<SteeringAgent>()
                    .filter {
                        it != this
                    }
                    .toMutableList()
            }
        }
    override val obstacles: ObstaclesProvider
        get() {
            val avoidanceDirection =
                if (velocity.lengthSquared() < 0.0001f) {
                    Vector3(0f)
                } else {
                    Vector3(velocity).normalize() * avoidanceDistance
                }
            val searchVolume = BoxAABB(
                Rect3d(
                    Vector3((position + avoidanceDirection) - avoidanceRadius),
                    Vector3((position + avoidanceDirection) + avoidanceRadius)
                )
            )

            return {
                navGrid
                    .buildSearchResults(searchVolume)
                    .filter {
                        it != this
                    }
                    .map {
                        when (it) {
                            is NavMeshAgent -> it.getAgentBounds()
                            is NavMeshObstacle -> it.getObstacleBounds()
                            else -> {
                                it.bounds()
                            }
                        }
                    }.toMutableList()
            }
        }
}