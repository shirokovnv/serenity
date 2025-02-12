package core.scene.navigation.path

import core.math.Vector3

data class Path(val nodes: List<PathNode>, val status: PathStatus) {
    fun isValid(): Boolean {
        return status == PathStatus.OK
    }

    fun waypoints(): List<Vector3> {
        return nodes.map { it.point }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Path

        if (status != other.status) return false

        if (nodes.isEmpty() && other.nodes.isEmpty()) return false
        if (nodes.size != other.nodes.size) return false
        for (i in nodes.indices) {
            if (nodes[i].point != other.nodes[i].point) return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = nodes.hashCode()
        result = 31 * result + status.hashCode()
        return result
    }
}