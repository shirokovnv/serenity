package core.scene.picking

import core.scene.Object

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

    fun pickings(): List<Pickable> = pickings.values.filter { it.isActive() && (it.owner() as Object).isActive() }

    fun count(): Int = pickings.values.filter { it.isActive() && (it.owner() as Object).isActive() }.size
}