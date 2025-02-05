package core.scene.picking

import core.ecs.Component

interface Pickable {
    val pickingKey: PickingKey
    val componentRef: Component?
}