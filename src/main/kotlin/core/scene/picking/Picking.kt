package core.scene.picking

import core.ecs.BaseComponent
import core.management.Disposable

class Picking: BaseComponent(), Pickable, Disposable {
    override val pickingKey: PickingKey = PickingKey()

    init {
        PickingContainer.add(this)
    }

    override fun dispose() {
        PickingContainer.remove(this)
    }
}