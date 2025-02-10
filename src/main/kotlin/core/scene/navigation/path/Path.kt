package core.scene.navigation.path

data class PathResult(val path: List<PathNode>?, val status: PathResultStatus) {
    fun isValid(): Boolean {
        return status == PathResultStatus.OK
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PathResult

        if (status != other.status) return false

        if (path == null && other.path == null) return false
        if (path?.size != other.path?.size) return false
        if (path != null && other.path != null) {
            for (i in path.indices) {
                if (path[i].point != other.path[i].point) return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        var result = path?.hashCode() ?: 0
        result = 31 * result + status.hashCode()
        return result
    }
}