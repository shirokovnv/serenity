package core.scene.picking

object PickingContainer {
    private val pickings = mutableMapOf<Int, Pickable>()

    fun add(picking: Pickable) {
        pickings[picking.pickingKey.id] = picking
    }

    fun remove(picking: Pickable) {
        pickings.remove(picking.pickingKey.id)
    }

    fun remove(pickingId: Int) {
        pickings.remove(pickingId)
    }

    fun remove(pickingKey: PickingKey) {
        pickings.remove(pickingKey.id)
    }

    fun clear() {
        pickings.clear()
    }

    fun pickings(): List<Pickable> = pickings.values.toList()

    fun count(): Int = pickings.size
}