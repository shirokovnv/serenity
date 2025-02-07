package modules.terrain.navigation

import core.math.*
import core.math.helpers.distanceSquared
import core.scene.Object
import core.scene.navigation.NavGrid
import core.scene.navigation.NavigatorInterface
import core.scene.navigation.agents.NavMeshAgent
import core.scene.navigation.path.PathNode
import core.scene.navigation.path.PathNodeIndex
import core.scene.navigation.path.PathResult
import core.scene.navigation.path.PathResultStatus
import core.scene.volumes.BoxAABB
import modules.terrain.heightmap.Heightmap

class TerrainNavigator(
    private val heightmap: Heightmap,
    private val grid: NavGrid
) : NavigatorInterface {

    companion object {
        private val upVector = Vector3(0f, 1f, 0f)
        private const val MAX_PATH_LENGTH = 4000
        private const val MAX_CLOSED_NODES_LENGTH = 20000
    }

    private val offsetXZ = Vector2(heightmap.worldOffset().x, heightmap.worldOffset().z)
    private val scaleXZ = Vector2(heightmap.worldScale().x, heightmap.worldScale().z)

    private val terrainBounds = Rect2d(Vector2(offsetXZ), offsetXZ + scaleXZ)
    private val nodes = mutableMapOf<PathNodeIndex, PathNode>()

    override fun calculatePath(start: Vector3, finish: Vector3, agent: NavMeshAgent): PathResult {
        if (outOfBounds(start, agent)) {
            return PathResult(null, PathResultStatus.NOT_EXISTS)
        }

        if (outOfBounds(finish, agent)) {
            return PathResult(null, PathResultStatus.NOT_EXISTS)
        }

        val startNode = evaluateNode(Vector3(start.x, 0f, start.z), agent)
        val endNode = evaluateNode(Vector3(finish.x, 0f, finish.z), agent)

        if (startNode == null || endNode == null) {
            return PathResult(null, PathResultStatus.NOT_EXISTS)
        }

        if (!startNode.isWalkable || !endNode.isWalkable) {
            return PathResult(null, PathResultStatus.NOT_EXISTS)
        }

        nodes.clear()

        val openedList = mutableListOf(startNode)
        val closedList = mutableListOf<PathNode>()

        startNode.gCost = 0f
        startNode.hCost = agent.getHeuristic().calculateDistanceCost(startNode, endNode)
        startNode.calculateFCost()

        while (openedList.isNotEmpty()) {
            val currentNode = getLowestFCostNode(openedList)

            if (currentNode == endNode) {
                return buildPath(endNode)
            }

            if (isNodesNear(currentNode, endNode, agent.stepSize)) {
                endNode.prevNode = currentNode
                return buildPath(endNode)
            }

            openedList.remove(currentNode)
            closedList.add(currentNode)

            if (closedList.size >= MAX_CLOSED_NODES_LENGTH) {
                return PathResult(null, PathResultStatus.TOO_LONG)
            }

            for (neighbor in getNeighbours(currentNode, agent)) {
                if (closedList.contains(neighbor)) continue

                if (!neighbor.isWalkable) {
                    closedList.add(neighbor)
                    continue
                }

                val tentativeGCost =
                    currentNode.gCost + agent.getHeuristic().calculateDistanceCost(currentNode, neighbor)

                if (tentativeGCost < neighbor.gCost) {
                    neighbor.prevNode = currentNode
                    neighbor.gCost = tentativeGCost
                    neighbor.hCost = agent.getHeuristic().calculateDistanceCost(neighbor, endNode)
                    neighbor.calculateFCost()

                    if (!openedList.contains(neighbor)) {
                        openedList.add(neighbor)
                    }
                }
            }
        }

        return PathResult(null, PathResultStatus.NOT_EXISTS)
    }

    override fun evaluatePoint(point: Vector3, agent: NavMeshAgent): Boolean {
        val bounds = BoxAABB(Rect3d(point - agent.radius, point + agent.radius))
        val searchResults = grid.buildSearchResults(bounds)

        return ensureSearchResultsContainAtMostSelf(agent, searchResults) &&
                ensureCanNavigateCell(agent, bounds.toRect2d())
    }

    override fun evaluateNode(point: Vector3, agent: NavMeshAgent): PathNode? {
        val bounds = BoxAABB(Rect3d(point - agent.radius, point + agent.radius))

        if (outOfBounds(point, agent)) {
            return null
        }

        // Check node in cache
        val indices = grid.getCellIndices(bounds.toRect2d())
        val pathNodeIndex = PathNodeIndex(indices)
        if (nodes.containsKey(pathNodeIndex)) {
            return nodes[pathNodeIndex]
        }

        // Create a new one
        val pathNode = PathNode(point)
        pathNode.isWalkable = evaluatePoint(point, agent)
        nodes[pathNodeIndex] = pathNode

        return pathNode
    }

    override fun outOfBounds(point: Vector3, agent: NavMeshAgent): Boolean {
        val minPoint = point - agent.radius
        val maxPoint = point + agent.radius
        val bounds = Rect2d(
            Vector2(minPoint.x, minPoint.y),
            Vector2(maxPoint.x, maxPoint.y)
        )

        return !IntersectionDetector.intersects(terrainBounds, bounds)
    }

    private fun isNodesNear(nodeA: PathNode, nodeB: PathNode, stepSize: Float): Boolean {
        return (distanceSquared(nodeA.point, nodeB.point) <= stepSize * stepSize)
    }

    private fun ensureSearchResultsContainAtMostSelf(
        agent: NavMeshAgent,
        searchResults: List<Object>
    ): Boolean {
        return searchResults.isEmpty() || (searchResults.size == 1 && searchResults.first() == agent.objectRef)
    }

    private fun ensureCanNavigateCell(agent: NavMeshAgent, cell: Rect2d): Boolean {
        val cellMin = cell.min
        val cellMax = cell.max

        var x = cellMin.x
        var y = cellMin.y

        while (x <= cellMax.x) {
            while (y <= cellMax.y) {
                val normal = heightmap.getInterpolatedNormal(x, y).normalize()
                if (normal.dot(upVector) < agent.maxSlope) {
                    return false
                }
                y += agent.stepSize
            }
            x += agent.stepSize
        }

        return true
    }

    private fun getLowestFCostNode(pathNodeList: MutableList<PathNode>): PathNode {
        var lowestFCostNode = pathNodeList[0]
        for (i in 1..<pathNodeList.size) {
            if (pathNodeList[i].fCost < lowestFCostNode.fCost) {
                lowestFCostNode = pathNodeList[i]
            }
        }
        return lowestFCostNode
    }

    private fun getNeighbours(
        currentNode: PathNode,
        agent: NavMeshAgent
    ): List<PathNode> {
        val neighbours = mutableListOf<PathNode?>()
        val p = currentNode.point
        p.y = 0f

        val stepSize = agent.stepSize

        val p0 = Vector3(p.x - stepSize, p.y, p.z - stepSize)
        val p1 = Vector3(p.x - stepSize, p.y, p.z)
        val p2 = Vector3(p.x, p.y, p.z - stepSize)
        val p3 = Vector3(p.x + stepSize, p.y, p.z)
        val p4 = Vector3(p.x, p.y, p.z + stepSize)
        val p5 = Vector3(p.x - stepSize, p.y, p.z + stepSize)
        val p6 = Vector3(p.x + stepSize, p.y, p.z - stepSize)
        val p7 = Vector3(p.x + stepSize, p.y, p.z + stepSize)

        val n0 = evaluateNode(p0, agent)
        val n1 = evaluateNode(p1, agent)
        val n2 = evaluateNode(p2, agent)
        val n3 = evaluateNode(p3, agent)
        val n4 = evaluateNode(p4, agent)
        val n5 = evaluateNode(p5, agent)
        val n6 = evaluateNode(p6, agent)
        val n7 = evaluateNode(p7, agent)

        neighbours.addAll(listOf(n0, n1, n2, n3, n4, n5, n6, n7))

        return neighbours.filterNotNull().filter { it.isWalkable }
    }

    private fun buildPath(endNode: PathNode): PathResult {
        val path = mutableListOf(endNode)
        var currentNode: PathNode? = endNode

        while (currentNode?.prevNode != null) {
            path.add(currentNode.prevNode!!)

            if (path.size > MAX_PATH_LENGTH) {
                return PathResult(restoreHeightsAndReversePath(path), PathResultStatus.TOO_LONG)
            }

            currentNode = currentNode.prevNode
        }

        return PathResult(restoreHeightsAndReversePath(path), PathResultStatus.OK)
    }

    private fun restoreHeightsAndReversePath(path: MutableList<PathNode>): List<PathNode> {
        path.forEach { p ->
            p.point.y = heightmap.getInterpolatedHeight(p.point.x, p.point.z) * heightmap.worldScale().y
        }

        path.reverse()
        return path
    }
}