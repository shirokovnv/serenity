package core.scene.navigation.agents

import core.math.Rect3d
import core.math.Vector3
import core.scene.Object
import core.scene.navigation.heuristics.PathHeuristicInterface
import core.scene.volumes.BoxAABB

abstract class BaseNavMeshAgent(
    private val initialWorldPosition: Vector3
): Object(), NavMeshAgent {
    protected abstract val pathHeuristic: PathHeuristicInterface

    override val objectRef: Object
        get() = this

    init {
        transform().setTranslation(initialWorldPosition)
    }

    override fun getAgentBounds(): BoxAABB {
        return BoxAABB(
            Rect3d(transform().translation() - radius, transform().translation() + radius),
        )
    }

    override fun getHeuristic(): PathHeuristicInterface {
        return pathHeuristic
    }
}