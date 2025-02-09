package core.scene.navigation.heuristics

import core.scene.navigation.path.PathNode

interface PathHeuristicInterface {
    fun calculateDistanceCost(a: PathNode, b: PathNode): Float
}