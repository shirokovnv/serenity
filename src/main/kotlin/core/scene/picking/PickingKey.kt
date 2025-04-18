package core.scene.picking

class PickingKey {
    companion object {
        private const val INSTANCE_ID_BITS = 15
        private const val MAX_INSTANCE_ID = (1 shl INSTANCE_ID_BITS) - 1
        private const val MAX_PICKING_OBJECTS = (1 shl (31 - INSTANCE_ID_BITS)) - 1

        private var counter = 1

        fun decodeCombinedKey(combinedKey: Int): Pair<Int, Int> {
            val instanceId = combinedKey shr (31 - INSTANCE_ID_BITS)
            val id = combinedKey and MAX_PICKING_OBJECTS
            return Pair(id, instanceId)
        }
    }

    val id = next()
    var instanceId: Int? = null
        set(value) {
            if (value != null && value > MAX_INSTANCE_ID) {
                throw IllegalStateException("Maximum InstanceId is: $MAX_INSTANCE_ID. Got: $value")
            }
            field = value
        }

    val combinedKey: Int
        get() = ((instanceId ?: 0) shl (31 - INSTANCE_ID_BITS)) or id

    @Synchronized
    private fun next(): Int {
        if (counter >= MAX_PICKING_OBJECTS) {
            throw IllegalStateException("Maximum number of picking objects exceeded.")
        }

        return counter++
    }
}