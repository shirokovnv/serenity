package core.scene.spatial

abstract class DynamicQuadTreeNode {
    companion object {
        const val MIN_LEVEL: Int = 0
        const val MAX_LEVEL: Int = 32
    }

    enum class Child {
        NW,
        NE,
        SW,
        SE
    }

    enum class Direction {
        N,
        S,
        W,
        E
    }

    var level: Int = 0
        set(value) {
            require(value in MIN_LEVEL..MAX_LEVEL) {
                "Level must be in range [$MIN_LEVEL, $MAX_LEVEL], but was $value"
            }
            field = value
        }

    protected var parent: DynamicQuadTreeNode? = null
    protected val children = mutableListOf<DynamicQuadTreeNode>()

    val isRoot: Boolean get() = parent == null
    val isLeaf: Boolean get() = children.isEmpty()

    fun addNode(node: DynamicQuadTreeNode, child: Child) {
        node.parent = this
        node.level = level + 1
        children.add(child.ordinal, node)
    }

    fun clear() {
        children.clear()
        level = 0
        parent = null
    }

    fun children(): List<DynamicQuadTreeNode> {
        return children
    }

    fun countLeaves(): Int {
        if (isLeaf) return 1

        return children().sumOf { child ->
            child.countLeaves()
        }
    }

    fun getNeighborOfGreaterOrEqualSize(direction: Direction): DynamicQuadTreeNode? {
        return when (direction) {
            Direction.N -> {
                if (this.parent == null) return null
                val p = this.parent!!

                if (p.children[Child.SW.ordinal] == this) {
                    p.children[Child.NW.ordinal]
                } else if (p.children[Child.SE.ordinal] == this) {
                    p.children[Child.NE.ordinal]
                } else {
                    val node = p.getNeighborOfGreaterOrEqualSize(Direction.N) ?: return null
                    if (node.isLeaf) node
                    else if (p.children[Child.NW.ordinal] == this)
                        node.children[Child.SW.ordinal]
                    else
                        node.children[Child.SE.ordinal]
                }
            }

            Direction.S -> {
                if (this.parent == null) return null
                val p = this.parent!!

                if (p.children[Child.NW.ordinal] == this) {
                    p.children[Child.SW.ordinal]
                } else if (p.children[Child.NE.ordinal] == this) {
                    p.children[Child.SE.ordinal]
                } else {
                    val node = p.getNeighborOfGreaterOrEqualSize(Direction.S) ?: return null
                    if (node.isLeaf) node
                    else if (p.children[Child.SW.ordinal] == this)
                        node.children[Child.NW.ordinal]
                    else
                        node.children[Child.NE.ordinal]
                }
            }

            Direction.E -> {
                if (this.parent == null) return null
                val p = this.parent!!

                if (p.children[Child.NW.ordinal] == this) {
                    p.children[Child.NE.ordinal]
                } else if (p.children[Child.SW.ordinal] == this) {
                    p.children[Child.SE.ordinal]
                } else {
                    val node = p.getNeighborOfGreaterOrEqualSize(Direction.E) ?: return null
                    if (node.isLeaf) node
                    else if (p.children[Child.NE.ordinal] == this)
                        node.children[Child.NW.ordinal]
                    else
                        node.children[Child.SW.ordinal]
                }
            }

            Direction.W -> {
                if (this.parent == null) return null
                val p = this.parent!!

                if (p.children[Child.NE.ordinal] == this) {
                    p.children[Child.NW.ordinal]
                } else if (p.children[Child.SE.ordinal] == this) {
                    p.children[Child.SW.ordinal]
                } else {
                    val node = p.getNeighborOfGreaterOrEqualSize(Direction.W) ?: return null
                    if (node.isLeaf) node
                    else if (p.children[Child.NW.ordinal] == this)
                        node.children[Child.NE.ordinal]
                    else
                        node.children[Child.SE.ordinal]
                }
            }
        }
    }

    fun findNeighborsOfSmallerSize(neighbor: DynamicQuadTreeNode?, direction: Direction): List<DynamicQuadTreeNode> {
        val candidates = mutableListOf<DynamicQuadTreeNode>()
        val neighbors = mutableListOf<DynamicQuadTreeNode>()

        neighbor?.let { candidates.add(it) }

        while (candidates.isNotEmpty()) {
            val current = candidates[0]
            if (current.isLeaf) {
                neighbors.add(current)
            } else {
                when (direction) {
                    Direction.N -> {
                        candidates.add(current.children[Child.SW.ordinal])
                        candidates.add(current.children[Child.SE.ordinal])
                    }

                    Direction.S -> {
                        candidates.add(current.children[Child.NW.ordinal])
                        candidates.add(current.children[Child.NE.ordinal])
                    }

                    Direction.E -> {
                        candidates.add(current.children[Child.NW.ordinal])
                        candidates.add(current.children[Child.SW.ordinal])
                    }

                    Direction.W -> {
                        candidates.add(current.children[Child.NE.ordinal])
                        candidates.add(current.children[Child.SE.ordinal])
                    }
                }
            }
            candidates.removeAt(0)
        }

        return neighbors
    }

    abstract fun split()
    abstract fun merge()
}