package core.scene.navigation.path

data class PathNodeIndex(val indices: IntArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        return indices.contentEquals((other as PathNodeIndex).indices)
    }

    override fun hashCode(): Int {
        return indices.contentHashCode()
    }
}