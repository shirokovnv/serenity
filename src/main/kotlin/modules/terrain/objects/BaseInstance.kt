package modules.terrain.objects

import core.scene.Object
import graphics.model.Model

abstract class BaseInstance(
    protected val model: Model,
    protected val instanceId: Int
) : Object() {
    fun model(): Model = model
    fun instanceId(): Int = instanceId
}