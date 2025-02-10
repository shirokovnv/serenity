package modules.terrain.navigation

import core.math.Matrix4
import core.math.Rect3d
import core.math.Vector3
import core.scene.navigation.NavGrid
import core.scene.navigation.agents.BaseNavMeshAgent
import core.scene.navigation.heuristics.DiagonalDistanceHeuristic
import core.scene.navigation.heuristics.PathHeuristicInterface
import core.scene.navigation.steering.NeighboursProvider
import core.scene.navigation.steering.SteeringAgent
import core.scene.volumes.BoxAABB

class TerrainAgent(
    initialWorldPosition: Vector3,
    val navGrid: NavGrid
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

    override var position: Vector3
        get() = transform().translation()
        set(value) {
            transform().setTranslation(value)
        }

    override var velocity: Vector3 = Vector3(0f)

    override var acceleration: Vector3 = Vector3(0f)

    override var maxSpeed: Float = 0.1f
    override var maxForce: Float = 0.05f

    override var orientation: Matrix4
        get() = TODO("Not yet implemented")
        set(value) {}

    override var perceptionDistance: Float = 25.0f

    override var target: Vector3 = Vector3(0f)
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
}