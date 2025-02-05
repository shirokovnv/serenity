package core.scene.picking

class PickingKey {
    companion object {
        private const val MAX_PICKING_OBJECTS = Int.MAX_VALUE
        private var counter = 1
    }

    val id = next()
    var instanceId: Int? = null

    @Synchronized
    private fun next(): Int {
        if (counter >= MAX_PICKING_OBJECTS) {
            throw IllegalStateException("Maximum number of picking objects exceeded.")
        }

        return counter++
    }
}