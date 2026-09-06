package core.scene.spatial

class QuadTreeCache(
    private val maxSize: Int,
    private val ttlMillis: Long
) {
    private val map = HashMap<QuadTreeKey, Pair<QuadTreeNode, Long>>(maxSize)
    private var allocationsCount = 0
    private var cacheHits = 0

    fun count(): Int = map.size
    fun allocations(): Int = allocationsCount
    fun hits(): Int = cacheHits

    fun getOrPut(key: QuadTreeKey, createNode: () -> QuadTreeNode): QuadTreeNode {
        val now = System.currentTimeMillis()

        map[key]?.let { (node, lastAccess) ->
            if (now - lastAccess > ttlMillis) {
                map.remove(key)
            } else {
                cacheHits++
                map[key] = node to now
                return node
            }
        }

        allocationsCount++
        val newNode = createNode()
        map[key] = newNode to now
        return newNode
    }

    fun remove(key: QuadTreeKey) {
        map.remove(key)
    }

    fun cleanupExpired() {
        val now = System.currentTimeMillis()
        val expiredKeys = mutableListOf<QuadTreeKey>()

        for (entry in map.entries) {
            val key = entry.key
            val (_, lastAccess) = entry.value

            if (now - lastAccess > ttlMillis) {
                expiredKeys.add(key)
            }
        }

        expiredKeys.forEach { key -> map.remove(key) }
    }
}