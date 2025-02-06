package core.scene.picking

import core.ecs.Component

interface Pickable : Component {
    val pickingKey: PickingKey
}